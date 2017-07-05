package org.fossasia.openevent.app.events.contract;

import org.fossasia.openevent.app.data.models.Event;

import java.util.List;

public interface IEventsView {

    void showProgressBar(boolean show);

    void onRefreshComplete();

    void showEvents(List<Event> events);

    void showEmptyView(boolean show);

    void showOrganiserName(String name);

    void showEventError(String error);

    void showOrganiserLoadError(String error);

}
