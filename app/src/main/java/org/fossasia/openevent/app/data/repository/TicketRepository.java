package org.fossasia.openevent.app.data.repository;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.Method;

import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.QueryHelper;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Event_Table;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.Ticket_Table;
import org.fossasia.openevent.app.data.models.query.TypeQuantity;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.repository.contract.ITicketRepository;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class TicketRepository extends Repository implements ITicketRepository {

    @Inject
    TicketRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        super(utilModel, databaseRepository, eventService);
    }

    @NonNull
    @Override
    public Observable<Ticket> getTickets(long eventId, boolean reload) {
        Observable<Ticket> diskObservable = Observable.defer(() ->
            databaseRepository.getItems(Ticket.class, Ticket_Table.event_id.eq(eventId))
        );

        Observable<Ticket> networkObservable = Observable.defer(() ->
            eventService.getTickets(eventId)
                .doOnNext(tickets -> databaseRepository
                    .deleteAll(Ticket.class)
                    .concatWith(databaseRepository.saveList(Ticket.class, tickets))
                    .concatWith(eventService.getAttendees(eventId)
                        .flatMapCompletable(attendees ->
                            databaseRepository.saveList(Attendee.class, attendees)))
                    .subscribe())
                .flatMapIterable(tickets -> tickets));

        return new AbstractObservableBuilder<Ticket>(utilModel)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<TypeQuantity> getTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .sum(Ticket_Table.quantity, "quantity")
            .from(Ticket.class)
            .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
            .where(Ticket_Table.event_id.withTable().eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<TypeQuantity> getSoldTicketsQuantity(long eventId) {
        return new QueryHelper<Ticket>()
            .select(Ticket_Table.type)
            .method(Method.count(), "quantity")
            .from(Ticket.class)
            .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
            .equiJoin(Event.class, Attendee_Table.event_id, Event_Table.id)
            .where(Ticket_Table.event_id.withTable().eq(eventId))
            .group(Ticket_Table.type)
            .toCustomObservable(TypeQuantity.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Single<Float> getTotalSale(long eventId) {
        return RXSQLite.rx(
            new QueryHelper<Ticket>()
                .sum(Ticket_Table.price, "price")
                .from(Ticket.class)
                .equiJoin(Attendee.class, Attendee_Table.ticket_id, Ticket_Table.id)
                .equiJoin(Event.class, Ticket_Table.event_id, Event_Table.id)
                .where(Ticket_Table.event_id.withTable().eq(eventId))
                .build())
            .query()
            .map(cursor -> {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    float result = cursor.getFloat(0);
                    cursor.close();
                    return result;
                }
                cursor.close();
                throw new NoSuchElementException();
            }).subscribeOn(Schedulers.io());
    }
}
