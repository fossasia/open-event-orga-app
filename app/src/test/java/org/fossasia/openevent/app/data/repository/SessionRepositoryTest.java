package org.fossasia.openevent.app.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.AbstractObservable;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.session.SessionApi;
import org.fossasia.openevent.app.data.session.SessionRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SessionRepositoryTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SessionRepositoryImpl sessionRepository;
    private static final Session SESSION = new Session();
    private static final Event EVENT = new Event();
    private static final long ID = 10L;

    @Mock
    private SessionApi sessionApi;
    @Mock private Repository repository;

    static {
        SESSION.setEvent(EVENT);
    }

    @Before
    public void setUp() {
        when(repository.observableOf(Session.class)).thenReturn(new AbstractObservable.AbstractObservableBuilder<>(repository));
        sessionRepository = new SessionRepositoryImpl(sessionApi, repository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSessionsWithReload() {
        when(repository.isConnected()).thenReturn(false);

        Observable<Session> sessionObservable = sessionRepository.getSessions(ID, true);

        sessionObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldReturnConnectionErrorOnGetSessionsWithNoneSaved() {
        when(repository.isConnected()).thenReturn(false);
        when(repository.getItems(eq(Session.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        Observable<Session> sessionObservable = sessionRepository.getSessions(ID, false);

        sessionObservable
            .test()
            .assertError(throwable -> throwable.getMessage().equals(Constants.NO_NETWORK));
    }

    @Test
    public void shouldCallGetSessionsServiceOnReload() {
        when(repository.isConnected()).thenReturn(true);
        when(sessionApi.getSessions(ID)).thenReturn(Observable.empty());

        sessionRepository.getSessions(ID, true).subscribe();

        verify(sessionApi).getSessions(ID);
    }

    @Test
    public void shouldCallGetSessionsServiceWithNoneSaved() {
        when(repository.isConnected()).thenReturn(true);
        when(sessionApi.getSessions(ID)).thenReturn(Observable.empty());
        when(repository.getItems(eq(Session.class), any(SQLOperator.class))).thenReturn(Observable.empty());

        sessionRepository.getSessions(ID, false).subscribe();

        verify(sessionApi).getSessions(ID);
    }

    @Test
    public void shouldSaveSessionsOnGet() {
        List<Session> sessions = new ArrayList<>();
        sessions.add(SESSION);

        when(repository.isConnected()).thenReturn(true);
        when(sessionApi.getSessions(ID)).thenReturn(Observable.just(sessions));
        when(repository.syncSave(eq(Session.class), eq(sessions), any(), any())).thenReturn(Completable.complete());

        sessionRepository.getSessions(ID, true).subscribe();

        verify(repository).syncSave(eq(Session.class), eq(sessions), any(), any());
    }
}
