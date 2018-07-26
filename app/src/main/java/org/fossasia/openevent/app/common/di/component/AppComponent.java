package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.di.module.AppModule;
import org.fossasia.openevent.app.common.di.module.android.ActivityBuildersModule;
import org.fossasia.openevent.app.data.attendee.AttendeeCheckInWork;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
    AndroidInjectionModule.class,
    ActivityBuildersModule.class,
    AppModule.class,
    FlavorModule.class
})
public interface AppComponent extends AndroidInjector<OrgaApplication> {

    void inject(OrgaApplication orgaApplication);

    void inject(AttendeeCheckInWork attendeeCheckInWork);
}
