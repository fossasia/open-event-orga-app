package org.fossasia.openevent.app.core.event.list;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.create.CreateEventActivity;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.FragmentEventListBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

import static org.fossasia.openevent.app.core.event.list.EventsPresenter.SORTBYDATE;
import static org.fossasia.openevent.app.core.event.list.EventsPresenter.SORTBYNAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends BaseFragment<EventsPresenter> implements EventsView {
    @Inject
    ContextUtils utilModel;

    @Inject
    Bus bus;

    @Inject
    Lazy<EventsPresenter> presenterProvider;

    private FragmentEventListBinding binding;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout refreshLayout;

    private EventsListAdapter eventListAdapter;
    private androidx.recyclerview.widget.RecyclerView.AdapterDataObserver adapterDataObserver;

    private Context context;
    private boolean initialized;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * parameters can be added in future if required so
     * which can be passed in bundle.
     *
     * @return A new instance of fragment EventListFragment.
     */
    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_list, container, false);
        return binding.getRoot();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByEventName:
                sortEvents(SORTBYNAME);
                return true;
            case R.id.sortByEventDate:
                sortEvents(SORTBYDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortEvents(int sortBy) {
        getPresenter().sortBy(sortBy);
        eventListAdapter.setSortByName(sortBy == SORTBYNAME);
        binding.setVariable(BR.events, getPresenter().getEvents());
        binding.executePendingBindings();
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public Lazy<EventsPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(this);
        binding.setEvents(getPresenter().getEvents());
        getPresenter().start();

        initialized = true;

        binding.createEventFab.setOnClickListener(view -> openCreateEventFragment());
    }

    public void openCreateEventFragment() {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getTitle() {
        return R.string.events;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        eventListAdapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    private void setupRecyclerView() {
        if (!initialized) {
            eventListAdapter = new EventsListAdapter(getPresenter().getEvents(), bus);

            recyclerView = binding.eventRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(eventListAdapter);
            recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(eventListAdapter);
            recyclerView.addItemDecoration(decoration);

            adapterDataObserver = new androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    decoration.invalidateHeaders();
                }
            };
        }
        eventListAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadUserEvents(true);
        });
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success)
            ViewUtils.showSnackbar(recyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Event> events) {
        binding.setVariable(BR.events, events);
        binding.executePendingBindings();
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

}
