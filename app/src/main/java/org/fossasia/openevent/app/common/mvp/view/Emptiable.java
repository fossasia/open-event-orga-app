package org.fossasia.openevent.app.common.mvp.view;

import java.util.List;

public interface Emptiable<T> {

    void showResults(List<T> items);

    void showEmptyView(boolean show);

}
