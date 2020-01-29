package com.eventyay.organizer.data.role;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoleApi {

    @POST("role-invites")
    Observable<RoleInvite> postRoleInvite(@Body RoleInvite roleInvite);

    @GET("events/{id}/role-invites")
    Observable<List<RoleInvite>> getRoles(@Path("id") long id);

    @DELETE("role-invites/{role_invite_id}")
    Completable deleteRole(@Path("role_invite_id") long roleInviteId);

    @PATCH("role-invites/{role_invite_id}")
    Observable<RoleInvite> updateRole(@Path("role_invite_id") long roleInviteId,@Body RoleInvite roleInvite);

    @GET("role-invites/{role_invite_id}")
    Observable<RoleInvite> getRole(@Path("role_invite_id") long roleId);
}
