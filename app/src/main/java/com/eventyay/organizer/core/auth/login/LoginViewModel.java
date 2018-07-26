package com.eventyay.organizer.core.auth.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;


import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.encryption.EncryptionService;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class LoginViewModel extends ViewModel {

    private final AuthService loginModel;
    private final EncryptionService encryptionService;
    private final Login login = new Login();
    private final HostSelectionInterceptor interceptor;
    private final Preferences sharedPreferenceModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String PREF_USER_PASSWORD = "user_password";
    private static final String PREF_USER_EMAIL = "user_email";

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Login> decryptedLogin = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn;
    private final SingleEventLiveData<Void> tokenSentAction = new SingleEventLiveData<>();
    private MutableLiveData<Set<String>> emailList;

    @Inject
    public LoginViewModel(AuthService loginModel, HostSelectionInterceptor interceptor,
                          Preferences sharedPreferenceModel, EncryptionService encryptionService) {
        this.loginModel = loginModel;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.encryptionService = encryptionService;
    }

    private void encryptUserCredentials() {
        String encryptedEmail = encryptionService.encrypt(login.getEmail());
        String encryptedPassword = encryptionService.encrypt(login.getPassword());
        sharedPreferenceModel.saveString(PREF_USER_EMAIL, encryptedEmail);
        sharedPreferenceModel.saveString(PREF_USER_PASSWORD, encryptedPassword);
    }

    //for logging into the app
    public void login() {
        compositeDisposable.add(loginModel.login(login)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> {
                    encryptUserCredentials();
                    isLoggedIn.setValue(true);
                },
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getLoginStatus() {
        if (isLoggedIn == null)
            isLoggedIn = new MutableLiveData<>();

        boolean loginValue = loginModel.isLoggedIn();

        if (loginValue) {
            isLoggedIn.setValue(true);
        }
        return isLoggedIn;
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    public LiveData<Login> getLogin() {
        if (decryptedLogin.getValue() == null) {
            login.setEmail(encryptionService.decrypt(sharedPreferenceModel.getString(PREF_USER_EMAIL, null)));
            login.setPassword(encryptionService.decrypt(sharedPreferenceModel.getString(PREF_USER_PASSWORD, null)));
            decryptedLogin.setValue(login);
            return decryptedLogin;
        }
        return decryptedLogin;
    }

    //fetching the email list from the shared preferences
    public LiveData<Set<String>> getEmailList() {
        if (emailList == null)
            emailList = new MutableLiveData<>();

        Set<String> emailSet = sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);

        if (emailSet != null) {
            emailList.setValue(emailSet);
        }
        return emailList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    @VisibleForTesting
    public Login getLoginModel() {
        return login;
    }

    public void requestToken() {
        RequestToken requestToken = new RequestToken();
        requestToken.setEmail(login.getEmail());

        compositeDisposable.add(loginModel.requestToken(requestToken)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(tokenSentAction::call,
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<Void> getTokenSentAction() {
        return tokenSentAction;
    }
}
