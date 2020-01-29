package com.eventyay.organizer.core.role.list;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.role.invite.RoleInviteFragment;
import com.eventyay.organizer.core.role.update.UpdateRoleFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.databinding.RolesFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.inject.Inject;

public class RoleListFragment extends BaseFragment implements RoleListView {

    private Context context;
    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RoleListAdapter rolesAdapter;
    private RolesFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private ActionMode actionMode;
    private int statusBarColor;
    private AlertDialog deleteDialog;

    private RoleListViewModel roleListViewModel;

    public static RoleListFragment newInstance(long eventId) {
        RoleListFragment fragment = new RoleListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.roles_fragment, container, false);
        roleListViewModel = ViewModelProviders.of(this, viewModelFactory).get(RoleListViewModel.class);

        binding.createRoleFab.setOnClickListener(v -> {
            roleListViewModel.resetToDefaultState();
            openRoleInviteFragment();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        roleListViewModel.getProgress().observe(this, this::showProgress);
        roleListViewModel.getSuccess().observe(this, this::showMessage);
        roleListViewModel.getError().observe(this, this::showError);
        roleListViewModel.getRolesLiveData().observe(this, this::showResults);
        roleListViewModel.getExitContextualMenuModeLiveData().observe(this, (exitContextualMenuMode) -> exitContextualMenuMode());
        roleListViewModel.getEnterContextualMenuModeLiveData().observe(this, (enterContextualMenuMode) -> enterContextualMenuMode());
        roleListViewModel.loadRoles(false);
        roleListViewModel.listenChanges();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        roleListViewModel.getRoleListChangeListener().stopListening();
    }

    private void setupRecyclerView() {
        rolesAdapter = new RoleListAdapter(roleListViewModel);

        RecyclerView recyclerView = binding.rolesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(rolesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            roleListViewModel.loadRoles(true);
        });
    }

    public void openRoleInviteFragment() {

        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, RoleInviteFragment.newInstance(eventId))
            .addToBackStack(null)
            .commit();
    }

    public ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_roles, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Hold current color of status bar
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                // Set the default color
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.color_top_surface));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    showDeleteDialog();
                    break;
                case R.id.update:
                    showUpdateDialog();
                    break;
                default:
                    return false;
            }
            return false;
        }
        public void showUpdateDialog() {
            final long selectedRoleId = roleListViewModel.getSelectedRoleId();
            if (selectedRoleId == -1)
                return;
            BottomSheetDialogFragment bottomSheetDialogFragment = UpdateRoleFragment.newInstance(roleListViewModel.getSelectedRoleId());
            bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
            exitContextualMenuMode();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode.finish();
            roleListViewModel.resetToDefaultState();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Return to old color of status bar
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
        }
    };

    public void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(String.format(getString(R.string.delete_confirmation_message),
                    getString(R.string.roles)))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    roleListViewModel.deleteSelectedRole();
                    roleListViewModel.resetToDefaultState();
                    exitContextualMenuMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        deleteDialog.show();
    }

    @Override
    public void enterContextualMenuMode() {
        actionMode = getActivity().startActionMode(actionCallback);
    }

    @Override
    public void exitContextualMenuMode() {
        if (actionMode != null)
            actionMode.finish();
    }

    @Override
    protected int getTitle() {
        return R.string.roles;
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
    }

    private void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void showResults(List<RoleInvite> items) {
        showEmptyView(items.isEmpty());
        rolesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }
}
