package org.fossasia.openevent.app.module.main;

import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.CurrencyUtils;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousResult;
import static org.fossasia.openevent.app.module.main.MainActivity.EVENT_KEY;

public class MainPresenter extends BasePresenter<IMainView> implements IMainPresenter {

    private final ISharedPreferenceModel sharedPreferenceModel;
    private final IAuthModel loginModel;
    private final IEventRepository eventRepository;
    private final IBus bus;
    private final ContextManager contextManager;

    private final long storedEventId;

    @Inject
    public MainPresenter(ISharedPreferenceModel sharedPreferenceModel, IAuthModel loginModel, IEventRepository eventRepository, IBus bus, ContextManager contextManager) {
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.loginModel = loginModel;
        this.eventRepository = eventRepository;
        this.bus = bus;
        this.contextManager = contextManager;

        storedEventId = sharedPreferenceModel.getLong(EVENT_KEY, -1);
    }

    @Override
    public void start() {
        if (storedEventId != -1) {
            eventRepository
                .getEvent(storedEventId, false)
                .compose(dispose(getDisposable()))
                .compose(erroneous(getView()))
                .subscribe(bus::pushSelectedEvent, Logger::logError);
        } else {
            getView().loadInitialPage(storedEventId);
        }

        bus.getSelectedEvent()
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(event -> {
                sharedPreferenceModel.setLong(EVENT_KEY, event.getId());
                CurrencyUtils.getCurrencySymbol(event.getPaymentCurrency())
                    .subscribe(ContextManager::setCurrency);
                getView().loadInitialPage(event.getId());
            }, Logger::logError);
    }

    @Override
    public void logout() {
        loginModel.logout()
            .compose(disposeCompletable(getDisposable()))
            .compose(erroneousCompletable(getView()))
            .subscribe(() -> {
                contextManager.clearOrganiser();
                getView().onLogout();
            }, Logger::logError);
    }
}
