package org.fossasia.openevent.app.data.models.delegates;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.models.Faq_Table;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.Ticket_Table;
import org.fossasia.openevent.app.utils.service.DateService;

import java.text.ParseException;
import java.util.List;

import timber.log.Timber;

public class EventDelegate implements IEventDelegate {

    private final Event event;

    public EventDelegate(Event event) {
        this.event = event;
    }

    @Override
    public int compareTo(@NonNull Event otherEvent) {
        return DateService.compareEventDates(event, otherEvent);
    }

    @Override
    public String getHeader() {
        try {
            return DateService.getEventStatus(event);
        } catch (ParseException e) {
            Timber.e(e);
        }

        return "INVALID";
    }

    @Override
    public long getHeaderId() {
        return getHeader().hashCode();
    }

    @Override
    @JsonIgnore
    public List<Ticket> getEventTickets() {
        List<Ticket> tickets = event.getTickets();
        if (tickets != null && !tickets.isEmpty()) {
            for (Ticket ticket : tickets)
                ticket.setEvent(event);

            return tickets;
        }

        event.setTickets(
            SQLite.select()
            .from(Ticket.class)
            .where(Ticket_Table.event_id.eq(event.getId()))
            .queryList());

        return tickets;
    }

    @Override
    @JsonIgnore
    public List<Faq> getEventFaqs() {
        List<Faq> faqs = event.getFaqs();
        if (faqs != null && !faqs.isEmpty()) {
            for (Faq faq : faqs)
                faq.setEvent(event);

            return faqs;
        }

        event.setFaqs(
            SQLite.select()
                .from(Faq.class)
                .where(Faq_Table.event_id.eq(event.getId()))
                .queryList());

        return faqs;
    }

}
