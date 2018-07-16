package org.fossasia.openevent.app.core.event.copyright;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface ICreateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();
}
