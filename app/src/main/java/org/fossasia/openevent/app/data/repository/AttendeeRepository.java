package org.fossasia.openevent.app.data.repository;

import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.core.attendee.checkin.job.AttendeeCheckInJob;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.db.QueryHelper;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AttendeeRepository extends Repository implements IAttendeeRepository {

    @Inject
    public AttendeeRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendee(long attendeeId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Attendee.class, Attendee_Table.id.eq(attendeeId))
                .take(1)
        );

        // There is no use case where we'll need to load single attendee from network
        return new AbstractObservableBuilder<Attendee>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(Observable.empty())
            .build();
    }

    @NonNull
    @Override
    public Observable<Attendee> getAttendees(long eventId, boolean reload) {
        Observable<Attendee> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Attendee.class, Attendee_Table.event_id.eq(eventId))
        );

        Observable<Attendee> networkObservable = Observable.defer(() ->
            eventService.getAttendees(eventId)
                .doOnNext(attendees -> syncSave(Attendee.class, attendees, Attendee::getId, Attendee_Table.id).subscribe())
                .flatMapIterable(attendees -> attendees));

        return new AbstractObservableBuilder<Attendee>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Long> getCheckedInAttendees(long eventId) {
        return new QueryHelper<Attendee>()
            .method(Method.count(), "sum")
            .from(Attendee.class)
            .equiJoin(Event.class, Event_Table.id, Attendee_Table.event_id)
            .where(Attendee_Table.isCheckedIn.eq(true))
            .and(Attendee_Table.event_id.eq(eventId))
            .count()
            .subscribeOn(Schedulers.io());
    }

    public Completable scheduleToggle(Attendee attendee) {
        return databaseRepository
            .update(Attendee.class, attendee)
            .concatWith(completableObserver -> {
                AttendeeCheckInJob.scheduleJob();
                if (!utilModel.isConnected())
                    completableObserver.onError(new Exception("No network present. Added to job queue"));
            })
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Attendee> toggleAttendeeCheckStatus(Attendee transitAttendee) {
        if (!utilModel.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return Observable.just(transitAttendee)
            .flatMap(attendee -> {
                // Remove relationships from attendee item
                attendee.setEvent(null);
                attendee.setTicket(null);
                attendee.setOrder(null);
                attendee.setCheckinTimes(DateUtils.formatDateToIso(LocalDateTime.now()));

                return eventService.patchAttendee(attendee.getId(), attendee);
            })
            .doOnNext(attendee -> databaseRepository
                .update(Attendee.class, attendee)
                .subscribe())
            .doOnError(throwable -> scheduleToggle(transitAttendee))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * A synchronous method for getting pending attendee check ins
     * @return Pending attendee check ins
     */
    @NonNull
    @Override
    public Observable<Attendee> getPendingCheckIns() {
        return databaseRepository
            .getItems(Attendee.class, Attendee_Table.checking.eq(new ObservableBoolean(true)));
    }

}
