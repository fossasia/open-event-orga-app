package org.fossasia.openevent.app.module.event.list;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.service.DateService;
import org.fossasia.openevent.app.module.event.list.contract.IEventsPresenter;
import org.fossasia.openevent.app.module.event.list.contract.IEventsView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;

public class EventsPresenter extends BasePresenter<IEventsView> implements IEventsPresenter {

    private final List<Event> events = new ArrayList<>();

    private final IEventRepository eventsDataRepository;

    public static final int SORTBYDATE = 0;
    public static final int SORTBYNAME = 1;

    @Inject
    public EventsPresenter(IEventRepository eventsDataRepository) {
        this.eventsDataRepository = eventsDataRepository;
    }

    @Override
    public void start() {
        loadUserEvents(false);
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }

    @Override
    public void sortBy(int criteria) {
        if (criteria == SORTBYNAME)
            Collections.sort(events, (e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
        else {
            Collections.sort(events, (e1, e2) -> DateService.compareEventDates(e1, e2));
        }
    }

    @Override
    public void loadUserEvents(boolean forceReload) {
        if (getView() == null)
            return;

        getEventSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toSortedList()
            .compose(emptiable(getView(), events))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (!forceReload && !events.isEmpty() && isRotated())
            return Observable.fromIterable(events);
        else
            return eventsDataRepository.getEvents(forceReload);
    }

    @VisibleForTesting
    public IEventsView getView() {
        return super.getView();
    }

}
