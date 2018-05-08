package org.fossasia.openevent.app.core.sponsor.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.sponsor.list.viewholder.SponsorsViewHolder;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;
import org.fossasia.openevent.app.ui.HeaderViewHolder;

import java.util.List;

public class SponsorsListAdapter extends RecyclerView.Adapter<SponsorsViewHolder>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final List<Sponsor> sponsors;

    public SponsorsListAdapter(SponsorsPresenter sponsorsPresenter) {
        this.sponsors = sponsorsPresenter.getSponsors();
    }

    @NonNull
    @Override
    public SponsorsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new SponsorsViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.sponsor_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(SponsorsViewHolder sponsorViewHolder, int position) {
        sponsorViewHolder.bindSponsor(sponsors.get(position));
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int position) {
        headerViewHolder.bindHeader(sponsors.get(position).getHeader());
    }

    @Override
    public long getHeaderId(int position) {
        return sponsors.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return sponsors.size();
    }

}
