package org.fossasia.openevent.app.common.app.lifecycle.view;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.app.lifecycle.contract.presenter.IBasePresenter;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;

import dagger.Lazy;

public abstract class BaseFragment<P extends IBasePresenter> extends Fragment {

    protected abstract @StringRes int getTitle();

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(getTitle()));
    }

    protected abstract Lazy<P> getPresenterProvider();

    protected P getPresenter() {
        return getPresenterProvider().get();
    }

    protected void setTitle(String title) {
        ViewUtils.setTitle(this, title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OrgaApplication.getRefWatcher(getActivity()).watch(this);
    }

}
