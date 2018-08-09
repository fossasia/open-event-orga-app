package org.fossasia.openevent.app.core.orders.create;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface CreateOrderView extends Progressive, Erroneous, Refreshable, Successful {
}
