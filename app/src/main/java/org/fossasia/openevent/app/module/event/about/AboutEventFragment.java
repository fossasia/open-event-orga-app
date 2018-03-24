package org.fossasia.openevent.app.module.event.about;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseFragment;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.AboutEventFragmentBinding;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;
import org.fossasia.openevent.app.module.event.copyright.CreateCopyrightFragment;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.Observable;

public class AboutEventFragment extends BaseFragment<IAboutEventPresenter> implements IAboutEventVew {

    public static final String EVENT_ID = "event_id";

    private AboutEventFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private long eventId;
    private boolean creatingCopyright = true;

    private ActionBar mainActionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private AppCompatActivity mainActivity;

    @Inject
    Lazy<IAboutEventPresenter> aboutEventPresenterProvider;
    @Inject
    IUtilModel utilModel;

    public AboutEventFragment() {
        //required empty constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId Event for which the Fragment is to be created.
     * @return A new instance of fragment AboutEventFragment.
     */
    public static AboutEventFragment newInstance(long eventId) {
        AboutEventFragment fragment = new AboutEventFragment();
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent()
            .inject(this);

        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null)
            eventId = arguments.getLong(EVENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.about_event_fragment, container, false);

        mainActivity = (AppCompatActivity) getActivity();
        drawerLayout = mainActivity.findViewById(R.id.drawer_layout);
        mainActionBar = mainActivity.getSupportActionBar();

        if (mainActionBar != null)
            mainActionBar.hide();
        mainActivity.setSupportActionBar(binding.toolbar);
        drawerToggle = new ActionBarDrawerToggle(
            getActivity(), drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
            Observable.just((binding.collapsingToolbar.getHeight() + verticalOffset) <
                (2 * ViewCompat.getMinimumHeight(binding.collapsingToolbar)))
                .distinctUntilChanged()
                .map(collapsed -> {
                    if (collapsed)
                        return getResources().getColor(android.R.color.black);
                    else
                        return getResources().getColor(android.R.color.white);
                }).subscribe(color -> {
                Drawable icon = binding.toolbar.getNavigationIcon();
                if (icon != null) icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }));

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
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            getPresenter().loadEvent(true);
            getPresenter().loadCopyright(true);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_about_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_change_copyright:
                if (creatingCopyright) {
                    BottomSheetDialogFragment bottomSheetDialogFragment = CreateCopyrightFragment.newInstance();
                    bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                }
                break;
            default:
                // No implementation
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!creatingCopyright) {
            MenuItem menuItem = menu.findItem(R.id.action_create_change_copyright);
            menuItem.setTitle(R.string.edit_copyright);
        }
    }

    @Override
    public Lazy<IAboutEventPresenter> getPresenterProvider() {
        return aboutEventPresenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.about_event_fragment;
    }

    @Override
    protected int getTitle() {
        return R.string.about;
    }

    @Override
    public void showResult(Event item) {
        binding.setEvent(item);
    }

    @Override
    public void showCopyright(Copyright copyright) {
        binding.setCopyright(copyright);
    }

    @Override
    public void changeCopyrightMenuItem() {
        creatingCopyright = false;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.mainContent, error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.setSupportActionBar(mainActivity.findViewById(R.id.toolbar));
        mainActionBar.show();
        drawerLayout.removeDrawerListener(drawerToggle);
    }
}
