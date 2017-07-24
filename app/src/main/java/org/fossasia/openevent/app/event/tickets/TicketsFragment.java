package org.fossasia.openevent.app.event.tickets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.lifecycle.BaseFragment;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.databinding.TicketsFragmentBinding;
import org.fossasia.openevent.app.event.tickets.contract.ITicketsPresenter;
import org.fossasia.openevent.app.event.tickets.contract.ITicketsView;
import org.fossasia.openevent.app.main.MainActivity;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class TicketsFragment extends BaseFragment<ITicketsPresenter> implements ITicketsView {

    private Context context;

    @Inject
    IUtilModel utilModel;

    @Inject
    Lazy<ITicketsPresenter> ticketsPresenter;

    private TicketsAdapter ticketsAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private TicketsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public TicketsFragment() {
        // Required empty public constructor
    }

    public static TicketsFragment newInstance(long eventId) {
        TicketsFragment fragment = new TicketsFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        OrgaApplication.getAppComponent(context)
            .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tickets_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            long eventId = getArguments().getLong(MainActivity.EVENT_KEY);
            getPresenter().attach(eventId, this);
        }
        getPresenter().start();

        setupRecyclerView();
        setupRefreshListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        ticketsAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    private void setupRecyclerView() {
        ticketsAdapter = new TicketsAdapter(getPresenter().getTickets());

        RecyclerView recyclerView = binding.ticketsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(ticketsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(ticketsAdapter);
        recyclerView.addItemDecoration(decoration);

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        };
        ticketsAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> getPresenter().loadTickets(true));
    }

    @Override
    public Lazy<ITicketsPresenter> getPresenterProvider() {
        return ticketsPresenter;
    }

    @Override
    public int getLoaderId() {
        return R.layout.tickets_fragment;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
        Snackbar.make(binding.ticketsRecyclerView, R.string.refresh_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResults(List<Ticket> items) {
        ticketsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

}
