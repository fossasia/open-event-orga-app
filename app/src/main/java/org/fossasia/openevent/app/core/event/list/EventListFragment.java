package org.fossasia.openevent.app.core.event.list;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.event.create.CreateEventActivity;
import org.fossasia.openevent.app.core.event.list.pager.ListPageFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.databinding.EventListFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.core.event.list.EventsViewModel.SORTBYDATE;
import static org.fossasia.openevent.app.core.event.list.EventsViewModel.SORTBYNAME;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EventListFragment extends BaseFragment implements EventsView {
    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    EventsViewModel eventsViewModel;

    private EventListFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static final String[] EVENT_TYPE = {"live", "past", "draft"};
    public static final String POSITION = "position";

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.event_list_fragment, container, false);

        eventsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(EventsViewModel.class);
        eventsViewModel.getSuccess().observe(this, this::onRefreshComplete);
        eventsViewModel.getError().observe(this, this::showError);
        eventsViewModel.getProgress().observe(this, this::showProgress);

        eventsViewModel.loadUserEvents(false);

        binding.tab.setupWithViewPager(binding.pager);

        binding.pager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return EVENT_TYPE.length;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = ListPageFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt(POSITION, position);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return EVENT_TYPE[position];
            }
        });

        binding.pager.setOnTouchListener((v, event) -> {
            binding.swipeContainer.setEnabled(false);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                    binding.swipeContainer.setEnabled(true);
            }
            return false;
        });

        binding.createEventFab.setOnClickListener(view -> openCreateEventFragment());

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
    }

    public void openCreateEventFragment() {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            eventsViewModel.loadUserEvents(true);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByEventName:
                eventsViewModel.sortBy(SORTBYNAME);
                return true;
            case R.id.sortByEventDate:
                eventsViewModel.sortBy(SORTBYDATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getTitle() {
        return R.string.events;
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        if (success) {
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
        }
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

}
