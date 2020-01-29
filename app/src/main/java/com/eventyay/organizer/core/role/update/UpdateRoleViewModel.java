package com.eventyay.organizer.core.role.update;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.role.Role;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.data.role.RoleRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class UpdateRoleViewModel extends ViewModel {

    private final RoleRepository roleRepository;
    private RoleInvite roleData = new RoleInvite();
    private Role role = new Role();

    @Inject
    public UpdateRoleViewModel(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<RoleInvite> roleLiveData = new SingleEventLiveData<>();


    public void loadRole(long roleId) {
        compositeDisposable.add(
            roleRepository
                .getRole(roleId, false)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() ->
                {
                    progress.setValue(false);
                    showRoles();
                })
                .subscribe(loadedRole -> this.roleData = loadedRole,
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString()))
        );

    }

    private void showRoles() {
        roleLiveData.setValue(roleData);
    }

    public LiveData<RoleInvite> getRoleLiveData() {
        return roleLiveData;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public RoleInvite getRole() {
        return roleData;
    }

    public void updateRole(long itemSelectedId) {
        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        roleData.setEvent(event);
        role.setId(itemSelectedId);
        roleData.setRole(role);

        compositeDisposable.add(
            roleRepository
                .updateRole(roleData)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedTrack -> {
                    success.setValue("Role Updated");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
