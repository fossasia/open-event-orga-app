package org.fossasia.openevent.app.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.models.Faq_Table;
import org.fossasia.openevent.app.data.network.EventService;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FaqRepository extends Repository implements IFaqRepository {

    @Inject
    public FaqRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @Override
    public Observable<Faq> getFaqs(long eventId, boolean reload) {
        Observable<Faq> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Faq.class, Faq_Table.event_id.eq(eventId))
        );

        Observable<Faq> networkObservable = Observable.defer(() ->
            eventService.getFaqs(eventId)
                .doOnNext(faqs -> {
                    Timber.d(faqs.toString());
                    databaseRepository
                    .deleteAll(Faq.class)
                    .concatWith(databaseRepository.saveList(Faq.class, faqs))
                    .subscribe(); })
                .flatMapIterable(faqs -> faqs));

        return new AbstractObservableBuilder<Faq>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Faq> createFaq(Faq faq) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventService
            .postFaq(faq)
            .doOnNext(created -> {
                created.setEvent(faq.getEvent());
                databaseRepository.save(Faq.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
