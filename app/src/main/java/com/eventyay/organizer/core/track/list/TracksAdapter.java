package com.eventyay.organizer.core.track.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.track.list.viewholder.TracksViewHolder;
import com.eventyay.organizer.data.tracks.Track;

import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksViewHolder> {
    private final List<Track> tracks;
    private final TracksPresenter tracksPresenter;

    public TracksAdapter(TracksPresenter tracksPresenter) {
        this.tracksPresenter = tracksPresenter;
        this.tracks = tracksPresenter.getTracks();
    }

    @NonNull
    @Override
    public TracksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        TracksViewHolder tracksViewHolder = new TracksViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.track_item, viewGroup, false), tracksPresenter);

        tracksViewHolder.setClickAction(tracksPresenter::click);
        tracksViewHolder.setLongClickAction(tracksPresenter::longClick);

        return tracksViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TracksViewHolder tracksViewHolder, int position) {
        tracksViewHolder.bind(tracks.get(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
}
