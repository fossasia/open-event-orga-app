package org.fossasia.openevent.app.common.mvp.view;

import androidx.fragment.app.Fragment;

import org.fossasia.openevent.app.common.mvp.presenter.BasePresenter;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public abstract class BaseInjectActivity<P extends BasePresenter> extends BaseActivity<P> implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<androidx.fragment.app.Fragment> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
