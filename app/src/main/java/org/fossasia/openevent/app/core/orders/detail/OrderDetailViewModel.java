package org.fossasia.openevent.app.core.orders.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.data.order.OrderRepository;
import org.fossasia.openevent.app.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;

public class OrderDetailViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public OrderDetailViewModel(OrderRepository orderRepository, EventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        progress.setValue(false);
    }

    public LiveData<Order> getOrder(String identifier, long eventId, boolean reload) {
        if (orderLiveData.getValue() != null && !reload)
            return orderLiveData;

        compositeDisposable.add(orderRepository.getOrder(identifier, reload)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(order -> orderLiveData.setValue(order),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        if (!reload) {
            getEvent(eventId);
        }

        return orderLiveData;
    }

    public void getEvent(long eventId) {
        compositeDisposable.add(eventRepository.getEvent(eventId, false)
            .compose(dispose(compositeDisposable))
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(event -> orderLiveData.getValue().setEvent(event),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    protected LiveData<Boolean> getProgress() {
        return progress;
    }

    protected LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
