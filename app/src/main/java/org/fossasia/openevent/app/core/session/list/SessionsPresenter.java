package org.fossasia.openevent.app.core.session.list;

import android.databinding.ObservableBoolean;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class SessionsPresenter extends AbstractDetailPresenter<Long, SessionsView> {

    private final List<Session> sessions = new ArrayList<>();
    private final Map<Long, ObservableBoolean> selectedSessions = new ConcurrentHashMap<>();
    private final SessionRepository sessionRepository;
    private final DatabaseChangeListener<Session> sessionChangeListener;
    private boolean isToolbarActive;

    @Inject
    public SessionsPresenter(SessionRepository sessionRepository, DatabaseChangeListener<Session> sessionChangeListener) {
        this.sessionRepository = sessionRepository;
        this.sessionChangeListener = sessionChangeListener;
    }

    @Override
    public void start() {
        loadSessions(false);
        listenChanges();
    }

    @Override
    public void detach() {
        super.detach();
        sessionChangeListener.stopListening();
        selectedSessions.clear();
    }

    public void loadSessions(boolean forceReload) {
        getSessionSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .compose(emptiable(getView(), sessions))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Session> getSessionSource(boolean forceReload) {
        if (!forceReload && !sessions.isEmpty() && isRotated())
            return Observable.fromIterable(sessions);
        else {
            return sessionRepository.getSessions(getId(), forceReload);
        }
    }

    private void listenChanges() {
        sessionChangeListener.startListening();
        sessionChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(sessionModelChange -> loadSessions(false), Logger::logError);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public Map<Long, ObservableBoolean> getSelectedSessions() {
        return selectedSessions;
    }

    public void deleteSession(Long sessionId) {
        sessionRepository
            .deleteSession(sessionId)
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                selectedSessions.remove(sessionId);
                Logger.logSuccess(sessionId);
            }, Logger::logError);
    }

    public void deleteSelectedSessions() {
        Observable.fromIterable(selectedSessions.entrySet())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> {
                getView().showMessage("Sessions Deleted");
                resetToolbarToDefaultState();
            })
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteSession(entry.getKey());
                }
            }, Logger::logError);
    }

    public void longClick(Session clickedSession) {
        if (isToolbarActive)
            click(clickedSession.getId());
        else {
            selectedSessions.get(clickedSession.getId()).set(true);
            isToolbarActive = true;
            getView().changeToDeletingMode();
        }
    }

    public void click(Long clickedSessionId) {
        if (isToolbarActive) {
            if (countSelected() == 1 && isSessionSelected(clickedSessionId).get()) {
                selectedSessions.get(clickedSessionId).set(false);
                resetToolbarToDefaultState();
            } else if (isSessionSelected(clickedSessionId).get())
                selectedSessions.get(clickedSessionId).set(false);
            else
                selectedSessions.get(clickedSessionId).set(true);
        }
    }

    public void resetToolbarToDefaultState() {
        isToolbarActive = false;
        getView().resetToolbar();
    }

    public ObservableBoolean isSessionSelected(Long sessionId) {
        if (!selectedSessions.containsKey(sessionId))
            selectedSessions.put(sessionId, new ObservableBoolean(false));

        return selectedSessions.get(sessionId);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    private int countSelected() {
        int count = 0;
        for (Long id : selectedSessions.keySet()) {
            if (selectedSessions.get(id).get())
                count++;
        }
        return count;
    }
}
