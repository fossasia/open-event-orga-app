package com.eventyay.organizer.core.attendee.list;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eventyay.organizer.BR;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.attendee.ScanningDecider;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;
import com.eventyay.organizer.core.attendee.list.listeners.AttendeeItemCheckInEvent;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.databinding.FragmentAttendeesBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.SearchUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.utils.ComparableItemListImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("PMD.TooManyMethods")
public class AttendeesFragment extends BaseFragment implements AttendeesView {

    private static final int SORTBYTICKET = 1;
    private static final int SORTBYNAME = 0;
    private static final long ITEMS_PER_PAGE = 20;

    private Context context;

    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FastAdapter<Attendee> fastAdapter;

    private final ScanningDecider scanningDecider = new ScanningDecider();

    private ItemAdapter<Attendee> fastItemAdapter;
    private FragmentAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;

    private static final long FIRST_PAGE = 1;

    private long currentPage = 1;
    private List<Attendee> attendeeList = new ArrayList<>();

    private AttendeesViewModel attendeesViewModel;

    private RecyclerView.AdapterDataObserver observer;

    private static final String FILTER_SYNC = "FILTER_SYNC";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId id of the event.
     * @return A new instance of fragment AttendeesFragment.
     */
    public static AttendeesFragment newInstance(long eventId) {
        AttendeesFragment fragment = new AttendeesFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        setHasOptionsMenu(true);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            fastAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_attendees, menu);
        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) search.getActionView();
        setupSearchListener();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterByNone:
                fastItemAdapter.filter("");
                return true;
            case R.id.filterBySync:
                fastItemAdapter.filter(FILTER_SYNC);
                return true;
            case R.id.sortByTicket:
                sortAttendees(SORTBYTICKET);
                return true;
            case R.id.sortByName:
                sortAttendees(SORTBYNAME);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortAttendees(int sortBy) {
        if (sortBy == SORTBYTICKET) {
            ((ComparableItemListImpl<Attendee>) fastItemAdapter.getItemList()).withComparator((Attendee a1, Attendee a2) -> a1.getTicket().getType().compareTo(a2.getTicket().getType()), true);
        } else {
            ((ComparableItemListImpl<Attendee>) fastItemAdapter.getItemList()).withComparator((Attendee a1, Attendee a2) -> a1.getFirstname().compareTo(a2.getFirstname()), true);
        }
        fastItemAdapter.setNewList(attendeeList, true);
        binding.setVariable(BR.attendees, attendeeList);
        binding.executePendingBindings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendees, container, false);
        attendeesViewModel = ViewModelProviders.of(this, viewModelFactory).get(AttendeesViewModel.class);

        binding.fabScanQr.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        binding.fabScanQr.setOnClickListener(v -> scanningDecider.openScanQRActivity(getActivity(), eventId));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSearchListener();
        setupRefreshListener();
        setupRecyclerView();
        attendeesViewModel.getProgress().observe(this, this::showProgress);
        attendeesViewModel.getError().observe(this, this::showError);
        attendeesViewModel.getAttendeesLiveData().observe(this, this::showResults);
        attendeesViewModel.getShowScanButtonLiveData().observe(this, this::showScanButton);
        attendeesViewModel.getUpdateAttendeeLiveData().observe(this, this::updateAttendee);
        attendeesViewModel.loadAttendeesPageWise(FIRST_PAGE, false);
        attendeesViewModel.listenChanges();
    }

    @Override
    protected int getTitle() {
        return R.string.attendees;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        if (searchView != null)
            searchView.setOnQueryTextListener(null);
    }

    private void setupSearchListener() {
        if (searchView == null)
            return;

        searchView.setQueryHint(getString(R.string.search_placeholder));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fastItemAdapter.filter(s.trim());
                return true;
            }
        });
    }

    private void setupRecyclerView() {
        fastItemAdapter = new ItemAdapter<>();
        fastItemAdapter.getItemFilter().setFilterPredicate(
            (attendee, query) -> {
                if (query == null)
                    return true;

                if (query.equals(FILTER_SYNC)) {
                    return attendee.checking;
                }
                return !SearchUtils.filter(
                    query.toString(),
                    attendee.getFirstname(),
                    attendee.getLastname(),
                    attendee.getEmail());
            }
        );

        fastAdapter = FastAdapter.with(Collections.singletonList(fastItemAdapter));
        fastAdapter.setHasStableIds(true);
        fastAdapter.addEventHook(new AttendeeItemCheckInEvent(this));

        RecyclerView recyclerView = binding.rvAttendeeList;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && binding.fabScanQr.isShown())
                    binding.fabScanQr.hide();

                if (!recyclerView.canScrollVertically(1)) {

                    if (recyclerView.getAdapter().getItemCount() > currentPage * ITEMS_PER_PAGE) {
                        currentPage++;
                    } else {
                        currentPage++;
                        attendeesViewModel.loadAttendeesPageWise(currentPage, true);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.getAdapter().getItemCount() != 0) {
                    binding.fabScanQr.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        SwipeController swipeController = new SwipeController(attendeesViewModel, attendeeList, context);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        };
        fastAdapter.registerAdapterDataObserver(observer);
        ViewUtils.setRecyclerViewScrollAwareFabBehaviour(recyclerView, binding.fabScanQr);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            attendeeList.clear();
            attendeesViewModel.loadAttendeesPageWise(FIRST_PAGE, true);
            currentPage = FIRST_PAGE;
        });
    }

    // View Implementation

    @Override
    public void showToggleDialog(long attendeeId) {
        BottomSheetDialogFragment bottomSheetDialogFragment = AttendeeCheckInFragment.newInstance(attendeeId);
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        // Nothing to do
    }

    @Override
    public void showScanButton(boolean show) {
        ViewUtils.showView(binding.fabScanQr, show);
    }

    @Override
    public void showResults(List<Attendee> attendees) {
        attendeeList.addAll(attendees);
        fastItemAdapter.setNewList(attendeeList, true);
        binding.setVariable(BR.attendees, attendeeList);
        binding.executePendingBindings();
    }

    @Override
    public void showEmptyView(boolean show) {
        if (currentPage > 1)
            return;

        ViewUtils.showView(binding.emptyView, show);
    }

    @Override
    public void updateAttendee(Attendee attendee) {
        int position = fastItemAdapter.getAdapterPosition(attendee);
        fastItemAdapter.getItemFilter().set(position, attendee);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public List<Attendee> getAttendeeList() {
        return attendeeList;
    }
}
