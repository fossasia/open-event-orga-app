package com.eventyay.organizer.core.role.update;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.role.RoleInvite;

public interface UpdateRoleView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setRole(RoleInvite roleInvite);
}
