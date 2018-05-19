package org.fossasia.openevent.app.core.feedback.list;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.feedback.Feedback;
import org.fossasia.openevent.app.databinding.FeedbackFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

public class FeedbackListFragment extends BaseFragment<FeedbackListPresenter> implements FeedbackListView {

    private Context context;
    private long eventId;

    @Inject
    Lazy<FeedbackListPresenter> feedbacksPresenter;

    private FeedbackListAdapter feedbacksAdapter;
    private FeedbackFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private boolean initialized;

    public static FeedbackListFragment newInstance(long eventId) {
        FeedbackListFragment fragment = new FeedbackListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.feedback_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();

        initialized = true;
    }

    @Override
    protected int getTitle() {
        return R.string.title_feedbacks;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        if (initialized)
            return;

        feedbacksAdapter = new FeedbackListAdapter(getPresenter());

        androidx.recyclerview.widget.RecyclerView recyclerView = binding.feedbacksRecyclerView;
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
        recyclerView.setAdapter(feedbacksAdapter);
        recyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));
    }


    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadFeedbacks(true);
        });
    }


    @Override
    public Lazy<FeedbackListPresenter> getPresenterProvider() {
        return feedbacksPresenter;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.feedbacksRecyclerView, R.string.refresh_complete);
    }

    @Override
    public void showResults(List<Feedback> items) {
        feedbacksAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }

}
