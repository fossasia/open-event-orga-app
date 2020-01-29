package com.eventyay.organizer.core.role.update;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseBottomSheetFragment;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.databinding.UpdateRoleBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.ui.ViewUtils.showView;

public class UpdateRoleFragment extends BaseBottomSheetFragment implements UpdateRoleView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private static final String ROLE_ID = "role_id";
    private UpdateRoleBinding binding;
    private UpdateRoleViewModel updateRoleViewModel;
    private long roleId;

    public static UpdateRoleFragment newInstance(long Id) {
        Bundle bundle = new Bundle();
        bundle.putLong(ROLE_ID, Id);
        UpdateRoleFragment updateRoleFragment = new UpdateRoleFragment();
        updateRoleFragment.setArguments(bundle);
        return updateRoleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.update_role, container, false);
        updateRoleViewModel = ViewModelProviders.of(this, viewModelFactory).get(UpdateRoleViewModel.class);

        Bundle bundle = getArguments();
        roleId = bundle.getLong(ROLE_ID);
        binding.btnSubmit.setOnClickListener(v -> {
            final long itemSelectedId = binding.updateRoleSelectRole.getSelectedItemPosition() + 1;
            updateRoleViewModel.updateRole(itemSelectedId);
        });

        binding.updateRoleEmail.setOnClickListener(v -> {
            Toast.makeText(inflater.getContext(), R.string.email_cant_change, Toast.LENGTH_SHORT).show();
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateRoleViewModel.getProgress().observe(this, this::showProgress);
        updateRoleViewModel.getSuccess().observe(this, this::onSuccess);
        updateRoleViewModel.getError().observe(this, this::showError);
        updateRoleViewModel.getDismiss().observe(this, (dismiss) -> dismiss());
        updateRoleViewModel.getRoleLiveData().observe(this, this::setRole);
        updateRoleViewModel.loadRole(roleId);
        binding.setRole(updateRoleViewModel.getRole());
        binding.updateRoleEmail.setKeyListener(null);
        setUpSpinner();

    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> rolesAdapter = ArrayAdapter
            .createFromResource(getActivity(), R.array.roles, android.R.layout.simple_spinner_item);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.updateRoleSelectRole.setAdapter(rolesAdapter);
    }

    @Override
    public void setRole(RoleInvite roleInvite) {
        binding.setRole(roleInvite);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
