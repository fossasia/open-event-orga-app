package org.fossasia.openevent.app.common.data.repository.contract;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.query.TypeQuantity;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface ITicketRepository {

    @NonNull
    Observable<Ticket> createTicket(Ticket ticket);

    @NonNull
    Observable<Ticket> getTicket(long ticketId, boolean reload);

    @NonNull
    Observable<Ticket> getTickets(long eventId, boolean reload);

    @NonNull
    Completable deleteTicket(long id);

    @NonNull
    Observable<TypeQuantity> getTicketsQuantity(long eventId);

    @NonNull
    Observable<TypeQuantity> getSoldTicketsQuantity(long eventId);

    @NonNull
    Single<Float> getTotalSale(long eventId);

}
