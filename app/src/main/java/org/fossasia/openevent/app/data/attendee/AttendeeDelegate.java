package org.fossasia.openevent.app.data.attendee;

import android.view.View;

import org.fossasia.openevent.app.core.attendee.list.viewholders.AttendeeViewHolder;
import org.fossasia.openevent.app.common.model.HeaderProvider;

import java.util.List;

public interface AttendeeDelegate extends Comparable<Attendee>, HeaderProvider {
    long getIdentifier();
    int getType();
    int getLayoutRes();
    AttendeeViewHolder getViewHolder(View view);
    void bindView(AttendeeViewHolder holder, List<Object> list);
    void unbindView(AttendeeViewHolder holder);
}
