package org.fossasia.openevent.app.core.auth.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.auth.SharedViewModel;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.databinding.SignUpFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import androidx.navigation.fragment.NavHostFragment;
import br.com.ilhasoft.support.validation.Validator;
import dagger.Lazy;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class SignUpFragment extends BaseFragment<SignUpPresenter> implements SignUpView {

    @Inject
    Lazy<SignUpPresenter> presenterProvider;

    @Inject
    ContextUtils utilModel;

    private SignUpFragmentBinding binding;
    private Validator validator;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        validator = new Validator(binding);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        sharedViewModel.getEmail().observe(this, email -> binding.getUser().setEmail(email));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(this);
        binding.setUser(getPresenter().getUser());

        binding.btnSignUp.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();
            if (!(getPresenter().arePasswordsEqual(password, confirmPassword))) {
                return;
            }

            String url = binding.url.baseUrl.getText().toString().trim();
            getPresenter().setBaseUrl(url, binding.url.overrideUrl.isChecked());
            getPresenter().signUp();
        });
        binding.loginLink.setOnClickListener(view -> openLoginPage());
    }

    private void openLoginPage() {
        sharedViewModel.setEmail(binding.getUser().getEmail());
        NavHostFragment.findNavController(this).navigate(R.id.loginFragment);

//        getFragmentManager().beginTransaction()
//            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//            .replace(R.id.fragment_container, new LoginFragment())
//            .commit();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        openLoginPage();
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public Lazy<SignUpPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    protected int getTitle() {
        return R.string.sign_up;
    }
}
