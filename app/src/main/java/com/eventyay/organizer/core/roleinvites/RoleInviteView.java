package com.eventyay.organizer.core.roleinvites;

public interface RoleInviteView {

    void showError(String error);

    void onSuccess(String message);

    void showProgress(boolean show);

    int getTitle();

    void dismiss();
}
