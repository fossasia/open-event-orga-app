package com.eventyay.organizer.core.auth.signup;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.auth.SharedViewModel;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.databinding.SignUpFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.core.settings.LegalPreferenceFragment.PRIVACY_POLICY_URL;
import static com.eventyay.organizer.core.settings.LegalPreferenceFragment.TERMS_OF_USE_URL;

public class SignUpFragment extends BaseFragment implements SignUpView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ContextUtils utilModel;

    private SignUpFragmentBinding binding;
    private Validator validator;
    private SharedViewModel sharedViewModel;
    private SignUpViewModel signUpViewModel;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        validator = new Validator(binding);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        sharedViewModel.getEmail().observe(this, email -> binding.getUser().setEmail(email));
        signUpViewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setUser(signUpViewModel.getUser());

        ValidateUtils.validate(binding.emailLayout, ValidateUtils::validateEmail, getResources().getString(R.string.email_validation_error));
        signUpViewModel.getProgress().observe(this, this::showProgress);
        signUpViewModel.getSuccess().observe(this, this::onSuccess);
        signUpViewModel.getError().observe(this, this::showError);

        binding.btnSignUp.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String password = binding.password.getText().toString();
            String confirmPassword = binding.confirmPassword.getText().toString();
            if (!(signUpViewModel.arePasswordsEqual(password, confirmPassword))) {
                return;
            }

            String url = binding.url.baseUrl.getText().toString().trim();
            signUpViewModel.setBaseUrl(url, binding.url.overrideUrl.isChecked());
            signUpViewModel.signUp();
        });

        binding.privacyPolicy.setOnClickListener(view -> openPrivacyPolicy());
        binding.termsOfUse.setOnClickListener(view -> openTermsOfUse());
        binding.emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start != 0) {
                    sharedViewModel.setEmail(s.toString());
                    getFragmentManager().popBackStack();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(PRIVACY_POLICY_URL));
        startActivity(intent);
    }

    private void openTermsOfUse() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(TERMS_OF_USE_URL));
        startActivity(intent);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    protected int getTitle() {
        return R.string.sign_up;
    }
}
