package com.eventyay.organizer.core.role.list;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.data.role.RoleRepository;
import com.eventyay.organizer.utils.ErrorUtils;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RoleListViewModel extends ViewModel {

    private final List<RoleInvite> roles = new ArrayList<>();
    private final RoleRepository roleRepository;
    private final DatabaseChangeListener<RoleInvite> roleListChangeListener;
    private final Map<RoleInvite, ObservableBoolean> selectedMap = new ConcurrentHashMap<>();
    private RoleInvite previousRole = new RoleInvite();
    private boolean isContextualModeActive;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<RoleInvite>> rolesLiveData = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> exitContextualMenuMode = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> enterContextualMenuMode = new SingleEventLiveData<>();

    private long eventId;

    @Inject
    public RoleListViewModel(RoleRepository roleRepository, DatabaseChangeListener<RoleInvite> roleListChangeListener) {
        this.roleRepository = roleRepository;
        this.roleListChangeListener = roleListChangeListener;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<RoleInvite>> getRolesLiveData() {
        return rolesLiveData;
    }

    public LiveData<Void> getExitContextualMenuModeLiveData() {
        return exitContextualMenuMode;
    }

    public LiveData<Void> getEnterContextualMenuModeLiveData() {
        return enterContextualMenuMode;
    }

    public Map<RoleInvite, ObservableBoolean> getSelectedMap() {
        return selectedMap;
    }

    public DatabaseChangeListener<RoleInvite> getRoleListChangeListener() {
        return roleListChangeListener;
    }

    public void loadRoles(boolean forceReload) {

        eventId = ContextManager.getSelectedEvent().getId();

        compositeDisposable.add(
            getRoleSource(forceReload)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .toList()
                .subscribe(roleList -> {
                    roles.clear();
                    roles.addAll(roleList);
                    rolesLiveData.setValue(roles);
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<RoleInvite> getRoleSource(boolean forceReload) {
        if (!forceReload && !roles.isEmpty()) {
            return Observable.fromIterable(roles);
        } else {
            return roleRepository.getRoles(eventId, forceReload);
        }
    }

    public void listenChanges() {
        roleListChangeListener.startListening();
        roleListChangeListener.getNotifier()
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.INSERT) || action.equals(BaseModel.Action.DELETE))
            .subscribeOn(Schedulers.io())
            .subscribe(roleModelChange -> loadRoles(false), Logger::logError);
    }

    public List<RoleInvite> getRoles() {
        return roles;
    }

    public void deleteRole(RoleInvite role) {
        roleRepository
            .deleteRole(role.getId())
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> {
                selectedMap.remove(role);
                loadRoles(true);
                Logger.logSuccess(role);
            }, Logger::logError);
    }

    public void deleteSelectedRole() {
        Observable.fromIterable(selectedMap.entrySet())
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(entry -> {
                if (entry.getValue().get()) {
                    deleteRole(entry.getKey());
                }
                success.setValue("Deleted Successfully");
            }, Logger::logError);
    }

    public void unselectRole(RoleInvite role) {
        if (role != null && selectedMap.containsKey(role))
            selectedMap.get(role).set(false);
    }

    public void resetToDefaultState() {
        isContextualModeActive = false;
        unSelectRoleList();
        exitContextualMenuMode.call();
    }

    public void onLongSelect(RoleInvite currentRole) {
        getRoleSelected(currentRole);
        if (!isContextualModeActive) {
            enterContextualMenuMode.call();
        }
        if (!previousRole.equals(currentRole)) {
            unselectRole(previousRole);
        }
        selectedMap.get(currentRole).set(true);
        previousRole = currentRole;
        isContextualModeActive = true;
    }

    public ObservableBoolean getRoleSelected(RoleInvite role) {
        if (!selectedMap.containsKey(role)) {
            selectedMap.put(role, new ObservableBoolean(false));
        }
        return selectedMap.get(role);
    }

    public Map<RoleInvite, ObservableBoolean> getIsSelected() {
        return selectedMap;
    }

    public void unSelectRoleList() {
        for (RoleInvite role : selectedMap.keySet()) {
            unselectRole(role);
        }
    }

    public long getSelectedRoleId() {
        for (RoleInvite role : selectedMap.keySet()) {
            if (selectedMap.get(role).get()) {
                return role.getId();
            }
        }
        return -1;
    }
}
