package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.IEventDataRepository;
import org.fossasia.openevent.app.contract.presenter.IEventDetailPresenter;
import org.fossasia.openevent.app.contract.view.IEventDetailView;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventDetailActivityPresenter implements IEventDetailPresenter {

    private long eventId;
    private IEventDetailView eventDetailView;
    private IEventDataRepository eventRepository;

    private long quantity, totalAttendees, totalCheckedIn;

    public EventDetailActivityPresenter(long eventId, IEventDetailView eventDetailView, IEventDataRepository eventRepository) {
        this.eventId = eventId;
        this.eventDetailView = eventDetailView;
        this.eventRepository = eventRepository;
    }

    @Override
    public void attach() {
        loadAttendees(eventId, false);
        loadTickets(eventId, false);
    }

    @Override
    public void detach() {
        eventDetailView = null;
    }

    @Override
    public void loadTickets(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventDetailView.showProgressBar(true);

        eventRepository
            .getEvent(eventId, forceReload)
            .subscribe(this::processEventAndDisplay,
                throwable -> {
                if(eventDetailView == null)
                    return;
                eventDetailView.showEventLoadError(throwable.getMessage());
                eventDetailView.showProgressBar(false);
            });
    }

    private void processEventAndDisplay(Event event) {
        if(eventDetailView == null)
            return;
        eventDetailView.showEventName(event.getName());

        String[] startDate = event.getStartTime().split("T");
        String[] endDate = event.getEndTime().split("T");

        eventDetailView.showDates(startDate[0], endDate[0]);
        eventDetailView.showTime(endDate[1]);

        List<Ticket> tickets = event.getTickets();

        if(tickets != null) {
            quantity = 0;
            for (Ticket thisTicket : tickets)
                quantity += thisTicket.getQuantity();
        }

        eventDetailView.showQuantityInfo(quantity, totalAttendees);

        eventDetailView.showProgressBar(false);
    }

    @Override
    public void loadAttendees(long eventId, boolean forceReload) {
        if(eventDetailView == null)
            return;

        eventRepository
            .getAttendees(eventId, forceReload)
            .subscribe(this::processAttendeesAndDisplay,
                throwable -> {
                if(eventDetailView == null)
                    return;
                eventDetailView.showEventLoadError(throwable.getMessage());
            });
    }

    private void processAttendeesAndDisplay(List<Attendee> attendees) {
        if(eventDetailView == null)
            return;

        totalAttendees = 0;
        totalCheckedIn = attendees.size();

        Observable.fromIterable(attendees)
            .filter(Attendee::isCheckedIn)
            .toList()
            .map(List::size)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(checkedIn -> {
                eventDetailView.showAttendeeInfo(checkedIn, totalAttendees);
                eventDetailView.showQuantityInfo(quantity, totalAttendees);
            });
    }

    public IEventDetailView getView() {
        return eventDetailView;
    }
}
