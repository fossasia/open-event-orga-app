package org.fossasia.openevent.app.core.auth.login;

import androidx.annotation.VisibleForTesting;

import org.fossasia.openevent.app.BuildConfig;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;

import java.util.Set;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class LoginPresenter extends AbstractBasePresenter<LoginView> {

    private final AuthService loginModel;
    private final Preferences sharedPreferenceModel;
    private final HostSelectionInterceptor interceptor;
    private final Login login = new Login();

    @Inject
    public LoginPresenter(AuthService loginModel, Preferences sharedPreferenceModel, HostSelectionInterceptor interceptor) {
        this.loginModel = loginModel;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.interceptor = interceptor;
    }

    @Override
    public void start() {
        if (getView() == null)
            return;

        if (loginModel.isLoggedIn()) {
            getView().onSuccess("Successfully logged in");
            return;
        }

        Set<String> emailList = getEmailList();
        if (emailList != null)
            getView().attachEmails(emailList);
    }

    public Login getLogin() {
        return login;
    }

    public void login() {
        loginModel.login(login)
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> getView().onSuccess("Successfully Logged In"), Logger::logError);
    }

    public void setBaseUrl(String url, boolean shouldSetDefaultUrl) {
        String baseUrl = shouldSetDefaultUrl ? BuildConfig.DEFAULT_BASE_URL : url;
        interceptor.setInterceptor(baseUrl);
    }

    private Set<String> getEmailList() {
        return sharedPreferenceModel.getStringSet(Constants.SHARED_PREFS_SAVED_EMAIL, null);
    }

    @VisibleForTesting
    public LoginView getView() {
        return super.getView();
    }

}
