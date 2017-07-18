package org.fossasia.openevent.app.common;

import android.support.annotation.CallSuper;

import org.fossasia.openevent.app.common.contract.presenter.IBasePresenter;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V> implements IBasePresenter<V> {
    private V view;
    private CompositeDisposable compositeDisposable;

    @Override
    @CallSuper
    public void attach(V view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    @CallSuper
    public void detach() {
        view = null;
        compositeDisposable.dispose();
    }

    protected V getView() {
        return view;
    }

    protected CompositeDisposable getDisposable() {
        return compositeDisposable;
    }

}
