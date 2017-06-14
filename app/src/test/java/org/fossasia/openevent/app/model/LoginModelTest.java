package org.fossasia.openevent.app.model;

import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.UserDetail;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LoginModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LoginModel loginModel;

    @Mock
    IUtilModel utilModel;

    @Mock
    EventService eventService;

    @Mock
    IDatabaseRepository databaseRepository;

    private String token = "TestToken";
    private String email = "test";
    private String password = "test";

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoxNDk1NzQ1ODAwLCJpZGVudGl0eSI6MzQ0fQ.NlZ9mrmEPyGpzQ-aIqauhwliYLh9GMiz11sG-EUaQ6I";
    private static final String UNEXPIRABLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE0OTU3NDU0MDAsImlhdCI6MTQ5NTc0NTQwMCwiZXhwIjoyNDk1ODMxODAwLCJpZGVudGl0eSI6MzQ0fQ.A_aC4hwK8sixZk4k9gzmzidO1wj2hjy_EH573uorK-E";

    @Before
    public void setUp() {
        loginModel = new LoginModel(utilModel, eventService, databaseRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldCacheLogin() {
        // Partial mocking
        LoginModel spied = Mockito.spy(loginModel);

        Mockito.doReturn(true).when(spied).isLoggedIn();
        Mockito.when(utilModel.getToken()).thenReturn(token);

        Observable<LoginResponse> responseObservable = spied.login(email, password);

        responseObservable
            .map(LoginResponse::getAccessToken)
            .test()
            .assertValue(token);

        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(Completable.complete());
        Mockito.when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);

        assertEquals(responseObservable.blockingFirst().getAccessToken(), token);

        Mockito.verify(eventService).login(Mockito.any(Login.class));
        // Should save token on object return
        Mockito.verify(utilModel).saveToken(token);
    }

    @Test
    public void shouldNotSaveTokenOnErrorResponse() {
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.error(new Throwable("Error")));

        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(Completable.complete());

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);
        responseObservable.test().assertErrorMessage("Error");

        Mockito.verify(eventService).login(Mockito.any(Login.class));
        // Should not save token on object return
        Mockito.verify(utilModel, Mockito.never()).saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        Mockito.when(utilModel.isConnected()).thenReturn(false);
        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(Completable.complete());

        Observable<LoginResponse> responseObservable = loginModel.login(email, password);

        responseObservable.test().assertErrorMessage(Constants.NO_NETWORK);

        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldSayLoggedOutOnNull() {
        Mockito.when(utilModel.getToken()).thenReturn(null);
        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(Completable.complete());

        assertFalse(loginModel.isLoggedIn());

        Mockito.verify(utilModel).getToken();
    }

    @Test
    public void shouldResetExpiredToken() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(completable);
        Mockito.when(utilModel.getToken()).thenReturn(EXPIRED_TOKEN);

        assertFalse(loginModel.isLoggedIn());
        Mockito.verify(utilModel).getToken();
        testObserver.assertSubscribed();
    }

    @Test
    public void shouldSayLoggedInOnUnexpired() {
        Mockito.when(utilModel.getToken()).thenReturn(UNEXPIRABLE_TOKEN);

        assertTrue(loginModel.isLoggedIn());

        Mockito.verify(utilModel).getToken();
    }

    @Test
    public void shouldClearTokenOnLogout() {
        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(Completable.complete());
        loginModel.logout().subscribe();

        Mockito.verify(utilModel).saveToken(null);
    }

    @Test
    public void shouldClearUserFromDatabaseOnLogout() {
        TestObserver testObserver = TestObserver.create();
        Completable completable = Completable.complete()
            .doOnSubscribe(testObserver::onSubscribe);

        //noinspection unchecked
        Mockito.when(databaseRepository.deleteAll(User.class, UserDetail.class)).thenReturn(completable);
        loginModel.logout().subscribe();

        Mockito.verify(utilModel).saveToken(null);
        testObserver.assertSubscribed();
    }
}
