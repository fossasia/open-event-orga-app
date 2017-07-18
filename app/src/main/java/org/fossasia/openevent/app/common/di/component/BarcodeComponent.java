package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.common.di.module.BarcodeModule;
import org.fossasia.openevent.app.common.di.module.DataModule;
import org.fossasia.openevent.app.common.di.module.NetworkModule;
import org.fossasia.openevent.app.common.di.module.PresenterModule;
import org.fossasia.openevent.app.qrscan.ScanQRActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    DataModule.class,
    NetworkModule.class,
    PresenterModule.class,
    BarcodeModule.class
})
public interface BarcodeComponent {

    void inject(ScanQRActivity scanQRActivity);

}
