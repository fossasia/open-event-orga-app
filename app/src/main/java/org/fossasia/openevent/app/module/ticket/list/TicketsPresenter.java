package org.fossasia.openevent.app.module.ticket.list;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;
import org.fossasia.openevent.app.module.ticket.list.contract.ITicketsPresenter;
import org.fossasia.openevent.app.module.ticket.list.contract.ITicketsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;

public class TicketsPresenter extends BaseDetailPresenter<Long, ITicketsView> implements ITicketsPresenter {

    private final List<Ticket> tickets = new ArrayList<>();
    private final ITicketRepository ticketRepository;
    private final IDatabaseChangeListener<Ticket> ticketChangeListener;

    @Inject
    public TicketsPresenter(ITicketRepository ticketRepository, IDatabaseChangeListener<Ticket> ticketChangeListener) {
        this.ticketRepository = ticketRepository;
        this.ticketChangeListener = ticketChangeListener;
    }

    @Override
    public void start() {
        loadTickets(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        ticketChangeListener.stopListening();
    }

    private void listenChanges() {
        ticketChangeListener.startListening();
        ticketChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT))
            .subscribeOn(Schedulers.io())
            .subscribe(ticketModelChange -> loadTickets(false), Logger::logError);
    }

    @Override
    public void loadTickets(boolean refresh) {
        ticketRepository
            .getTickets(getId(), refresh)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), refresh))
            .toSortedList()
            .compose(emptiable(getView(), tickets))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    @Override
    public List<Ticket> getTickets() {
        return tickets;
    }
}
