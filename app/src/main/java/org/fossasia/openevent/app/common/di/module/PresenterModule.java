package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.chart.ChartPresenter;
import org.fossasia.openevent.app.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.event.attendees.AttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.detail.EventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.tickets.TicketsPresenter;
import org.fossasia.openevent.app.event.tickets.contract.ITicketsPresenter;
import org.fossasia.openevent.app.events.EventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.login.LoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.main.MainPresenter;
import org.fossasia.openevent.app.main.contract.IMainPresenter;
import org.fossasia.openevent.app.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PresenterModule {

    @Binds
    abstract ILoginPresenter bindsLoginPresenter(LoginPresenter loginPresenter);

    @Binds
    abstract IMainPresenter bindsMainPresenter(MainPresenter mainPresenter);

    @Binds
    abstract IEventsPresenter bindsEventsPresenter(EventsPresenter eventsPresenter);

    @Binds
    abstract IEventDetailPresenter bindsEventDetailPresenter(EventDetailPresenter eventDetailPresenter);

    @Binds
    abstract IChartPresenter bindsChartPresenter(ChartPresenter chartPresenter);

    @Binds
    abstract IAttendeesPresenter bindsAttendeePresenter(AttendeesPresenter attendeesPresenter);

    @Binds
    abstract IScanQRPresenter bindsScanQRPresenter(ScanQRPresenter scanQRPresenter);

    @Binds
    abstract IAttendeeCheckInPresenter bindsAttendeeCheckInPresenter(AttendeeCheckInPresenter attendeeCheckInPresenter);

    @Binds
    abstract ITicketsPresenter bindsTicketsPresenter(TicketsPresenter ticketsPresenter);

}
