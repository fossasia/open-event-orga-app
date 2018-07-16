package org.fossasia.openevent.app.core.speakerscall.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;
import org.fossasia.openevent.app.databinding.SpeakersCallFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class SpeakersCallFragment extends BaseFragment<SpeakersCallPresenter> implements SpeakersCallView {

    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    Lazy<SpeakersCallPresenter> speakersCallPresenter;

    private SpeakersCallFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static SpeakersCallFragment newInstance(long eventId) {
        SpeakersCallFragment fragment = new SpeakersCallFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.speakers_call_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    protected int getTitle() {
        return R.string.speakers_call;
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
            getPresenter().loadSpeakersCall(true);
        });
    }

    @Override
    public Lazy<SpeakersCallPresenter> getPresenterProvider() {
        return speakersCallPresenter;
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
        if (success)
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
    }

    @Override
    public void showResult(SpeakersCall speakersCall) {
        if (speakersCall == null) {
            ViewUtils.showView(binding.emptyView, true);
            return;
        }

        binding.setSpeakersCall(speakersCall);
        binding.executePendingBindings();
    }
}
