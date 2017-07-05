package org.fossasia.openevent.app.events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.databinding.EventSubheaderLayoutBinding;
import org.fossasia.openevent.app.events.viewholders.EventsHeaderViewHolder;
import org.fossasia.openevent.app.main.listeners.OnEventLoadedListener;
import org.fossasia.openevent.app.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder> implements StickyRecyclerHeadersAdapter<EventsHeaderViewHolder>{

    private static final int LIVE_EVENT = 1;
    private static final int PAST_EVENT = 2;
    private static final int UPCOMING_EVENT = 3;

    private static final String HEADER_LIVE = "LIVE";
    private static final String HEADER_PAST = "PAST";
    private static final String HEADER_UPCOMING = "UPCOMING";

    private List<Event> events;
    private Context context;

    EventsListAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        EventLayoutBinding binding = EventLayoutBinding.inflate(layoutInflater, parent, false);
        return new EventRecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final EventRecyclerViewHolder holder, int position) {
        final Event thisEvent = events.get(position);
        holder.bind(thisEvent);
    }

    @Override
    public long getHeaderId(int position) {
        Event event = events.get(position);
        try {
            return getEventStatus(event);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public EventsHeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new EventsHeaderViewHolder(EventSubheaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(EventsHeaderViewHolder holder, int position) {
        Event event = events.get(position);
        try {
            switch (getEventStatus(event)) {
                case LIVE_EVENT:
                    holder.bindHeader(HEADER_LIVE);
                    break;
                case PAST_EVENT:
                    holder.bindHeader(HEADER_PAST);
                    break;
                case UPCOMING_EVENT:
                    holder.bindHeader(HEADER_UPCOMING);
                    break;
                default:
                    holder.bindHeader(HEADER_LIVE);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private int getEventStatus(Event event) throws ParseException {
        DateUtils dateUtils = new DateUtils();
        Date startDate = dateUtils.parse(event.getStartTime());
        Date endDate = dateUtils.parse(event.getEndTime());
        Date now = new Date();
        if (now.after(startDate)) {
            if (now.before(endDate)) {
                return LIVE_EVENT;
            } else {
                return PAST_EVENT;
            }
        } else {
            return UPCOMING_EVENT;
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder{
        private final EventLayoutBinding binding;
        private final Context context;
        private Event event;

        EventRecyclerViewHolder(EventLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(view -> ((OnEventLoadedListener)context).onEventLoaded(event, true));
        }

        public void bind(Event event) {
            this.event = event;
            binding.setEvent(event);
            binding.executePendingBindings();
        }

    }

}
