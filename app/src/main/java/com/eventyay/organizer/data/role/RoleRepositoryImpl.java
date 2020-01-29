package com.eventyay.organizer.data.role;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;

import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RoleRepositoryImpl implements RoleRepository {

    private final RoleApi roleApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public RoleRepositoryImpl(RoleApi roleApi, Repository repository) {
        this.roleApi = roleApi;
        this.repository = repository;
    }

    @Override
    public Observable<RoleInvite> sendRoleInvite(RoleInvite roleInvite) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return roleApi
            .postRoleInvite(roleInvite)
            .doOnNext(inviteSent -> Timber.d(String.valueOf(inviteSent)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<RoleInvite> getRoles(long eventId, boolean reload) {
        Observable<RoleInvite> diskObservable = Observable.defer(() ->
            repository.getItems(RoleInvite.class, RoleInvite_Table.event_id.eq(eventId))
        );

        Observable<RoleInvite> networkObservable = Observable.defer(() ->
            roleApi.getRoles(eventId)
                .doOnNext(roles -> repository
                    .syncSave(RoleInvite.class, roles, RoleInvite::getId, RoleInvite_Table.id)
                    .subscribe())
                .flatMapIterable(roles -> roles));

        return repository.observableOf(RoleInvite.class)
            .reload(reload)
            .withRateLimiterConfig("Role", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Completable deleteRole(long roleInviteId) {
        if (!repository.isConnected()) {
            return Completable.error(new Throwable(Constants.NO_NETWORK));
        }

        return roleApi.deleteRole(roleInviteId)
            .doOnComplete(() -> repository
                .delete(Role.class, RoleInvite_Table.id.eq(roleInviteId))
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<RoleInvite> updateRole(RoleInvite roleUpdate) {
        if(!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }
        return roleApi
            .updateRole(roleUpdate.getId(),roleUpdate)
            .doOnNext(roleUpdated -> repository
                .update(RoleInvite.class,roleUpdated)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    }

    @NonNull
    @Override
    public Observable<RoleInvite> getRole(long roleId,boolean reload) {
        Observable<RoleInvite> diskObservable = Observable.defer(() ->
            repository.getItems(RoleInvite.class,RoleInvite_Table.id.eq(roleId)).take(1));

        Observable<RoleInvite> networkObservable = Observable.defer(()->
            roleApi.getRole(roleId)
                .doOnNext(roleInvite -> repository
                    .save(RoleInvite.class,roleInvite).subscribe()));

        return repository.observableOf(RoleInvite.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();

    }
}
