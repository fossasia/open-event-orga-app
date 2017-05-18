package org.fossasia.openevent.app;

import org.fossasia.openevent.app.contract.model.IUtilModel;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.network.api.EventService;
import org.fossasia.openevent.app.data.network.api.RetrofitLoginModel;
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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RetrofitLoginModelTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private RetrofitLoginModel retrofitLoginModel;

    @Mock
    IUtilModel utilModel;

    @Mock
    EventService eventService;

    private String token = "TestToken";
    private String email = "test";
    private String password = "test";

    @Before
    public void setUp() {
        retrofitLoginModel = new RetrofitLoginModel(utilModel);
        retrofitLoginModel.setEventService(eventService);
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
        Mockito.when(utilModel.isLoggedIn()).thenReturn(true);
        Mockito.when(utilModel.getToken()).thenReturn(token);

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

        assertEquals(responseObservable.blockingFirst().getAccessToken(), token);

        Mockito.verifyNoMoreInteractions(eventService);
    }

    @Test
    public void shouldCallServiceOnCacheMiss() {
        Mockito.when(utilModel.isConnected()).thenReturn(true);
        Mockito.when(eventService.login(Mockito.any(Login.class)))
            .thenReturn(Observable.just(new LoginResponse(token)));

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

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

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);
        responseObservable.test().assertErrorMessage("Error");

        Mockito.verify(eventService).login(Mockito.any(Login.class));
        // Should not save token on object return
        Mockito.verify(utilModel, Mockito.never()).saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Test
    public void shouldSendErrorOnNetworkDown() {
        Mockito.when(utilModel.isConnected()).thenReturn(false);

        Observable<LoginResponse> responseObservable = retrofitLoginModel.login(email, password);

        responseObservable.test().assertErrorMessage(Constants.NO_NETWORK);

        Mockito.verifyNoMoreInteractions(eventService);
    }


}
