package org.fossasia.openevent.app.event.detail;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.result;

public class EventDetailPresenter extends BaseDetailPresenter<Long, IEventDetailView> implements IEventDetailPresenter {

    private Event event;
    private IEventRepository eventRepository;
    private IAttendeeRepository attendeeRepository;
    private TicketAnalyser ticketAnalyser;

    @Inject
    public EventDetailPresenter(IEventRepository eventRepository, IAttendeeRepository attendeeRepository, TicketAnalyser ticketAnalyser) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public void attach(Long eventId, IEventDetailView eventDetailView) {
        super.attach(eventId, eventDetailView);
    }

    @Override
    public void start() {
        loadDetails(false);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void loadDetails(boolean forceReload) {
        if (getView() == null)
            return;

        eventRepository
            .getEvent(getId(), forceReload)
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .flatMap(loadedEvent -> {
                this.event = loadedEvent;

                ticketAnalyser.analyseTotalTickets(event);

                return attendeeRepository.getAttendees(getId(), forceReload);
            })
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .subscribe(attendees -> ticketAnalyser.analyseSoldTickets(event, attendees), Logger::logError);
    }

    @VisibleForTesting
    public IEventDetailView getView() {
        return super.getView();
    }

    @VisibleForTesting
    public Event getEvent() {
        return event;
    }
}
