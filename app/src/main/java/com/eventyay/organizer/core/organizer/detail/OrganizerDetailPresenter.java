package com.eventyay.organizer.core.organizer.detail;

import com.eventyay.organizer.common.mvp.presenter.AbstractBasePresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.ResendVerificationMail;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.data.user.UserRepositoryImpl;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class OrganizerDetailPresenter extends AbstractBasePresenter<OrganizerDetailView> {

    private final UserRepositoryImpl userRepository;
    private final AuthService authService;
    private final ResendVerificationMail resendVerificationMail = new ResendVerificationMail();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private User user;

    @Inject
    public OrganizerDetailPresenter(UserRepositoryImpl userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void start() {
        loadOrganizer(false);
    }

    public void loadOrganizer(boolean forceReload) {
        getOrganizerSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .subscribe(loadedUser -> {
                this.user = loadedUser;
                resendVerificationMail.setEmail(user.getEmail());
                getView().showResult(user);
            }, Logger::logError);
    }

    public void resendVerificationMail() {
        compositeDisposable.add(
            authService.resendVerificationMail(resendVerificationMail)
                .doOnSubscribe(disposable -> getView().showProgress(true))
                .doFinally(() -> getView().showProgress(false))
                .subscribe(resendMailResponse -> getView().showSnackbar("Verification Mail Resent"),
                    throwable -> {
                        getView().showError(ErrorUtils.getErrorDetails(throwable).toString());
                        Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
                    }));
    }

    //Method for storing user uploaded image in temporary location
    public void uploadImage(ImageData imageData) {
        compositeDisposable.add(
            userRepository
                .uploadOrganizerImage(imageData)
                .doOnSubscribe(disposable -> getView().showProgress(true))
                .doFinally(() -> getView().showProgress(false))
                .subscribe(uploadedImage -> {
                    getView().showSnackbar("Image Uploaded Successfully");
                    Timber.e(uploadedImage.getUrl());
                    user.setAvatarUrl(uploadedImage.getUrl());
                    updateOrganizer();
                }, Logger::logError));
    }

    public void updateOrganizer() {
        compositeDisposable.add(
            userRepository.updateUser(user)
                .doOnSubscribe(disposable -> getView().showProgress(true))
                .doFinally(() -> getView().showProgress(false))
                .subscribe(updatedUser -> {
                    getView().showSnackbar("User Updated");
                    loadOrganizer(false);
                }, throwable -> getView().showError(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<User> getOrganizerSource(boolean forceReload) {
        if (user != null && !forceReload && isRotated()) {
            return Observable.just(user);
        } else {
            return userRepository.getOrganizer(forceReload);
        }
    }
}
