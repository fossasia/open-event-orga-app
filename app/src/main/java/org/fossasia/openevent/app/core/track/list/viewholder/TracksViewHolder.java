package org.fossasia.openevent.app.core.track.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.core.track.list.TracksPresenter;
import org.fossasia.openevent.app.data.tracks.Track;
import org.fossasia.openevent.app.databinding.TrackItemBinding;

public class TracksViewHolder extends RecyclerView.ViewHolder {
    private final TrackItemBinding binding;
    private Track track;
    private TracksPresenter tracksPresenter;

    private Pipe<Track> longClickAction;
    private Pipe<Long> clickAction;

    public TracksViewHolder(TrackItemBinding binding, TracksPresenter tracksPresenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.tracksPresenter = tracksPresenter;

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                longClickAction.push(track);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(track.getId());
        });
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }

    public void setLongClickAction(Pipe<Track> longClickAction) {
        this.longClickAction = longClickAction;
    }
    public void bind(Track track) {
        this.track = track;
        binding.setTrack(track);
        binding.setTracksPresenter(tracksPresenter);
        binding.executePendingBindings();
    }
}
