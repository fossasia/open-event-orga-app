package org.fossasia.openevent.app.data.ticket;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.utils.CompareUtils;

@SuppressWarnings("ComparableType")
public class TicketDelegateImpl implements TicketDelegate {

    private final Ticket ticket;

    public TicketDelegateImpl(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public int compareTo(@NonNull Ticket otherOne) {
        return CompareUtils.compareCascading(ticket, otherOne, Ticket::getType);
    }

}
