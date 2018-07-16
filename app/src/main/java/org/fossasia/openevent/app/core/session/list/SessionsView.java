package org.fossasia.openevent.app.core.session.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.session.Session;

public interface SessionsView extends Progressive, Erroneous, Refreshable, Emptiable<Session> {

    void showMessage(String message);

    void changeToolbarMode(boolean editMode, boolean deleteMode);

    void openUpdateSessionFragment(long sessionId);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
