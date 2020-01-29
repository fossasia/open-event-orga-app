package com.eventyay.organizer.data.role;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface RoleRepository {

    Observable<RoleInvite> sendRoleInvite(RoleInvite roleInvite);

    Observable<RoleInvite> getRoles(long eventId, boolean forceReload);

    Completable deleteRole(long roleInviteId);

    Observable<RoleInvite> updateRole(RoleInvite roleUpdate);

    Observable<RoleInvite> getRole(long roleId,boolean forceReload);
}
