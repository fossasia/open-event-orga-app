package org.fossasia.openevent.app.common.app.lifecycle.contract.presenter;

public interface IDetailPresenter<K, V> extends IBasePresenter {

    void attach(K key, V view);

}
