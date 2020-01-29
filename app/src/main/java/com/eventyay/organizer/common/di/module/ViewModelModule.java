package com.eventyay.organizer.common.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.eventyay.organizer.common.di.OrgaViewModelFactory;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInViewModel;
import com.eventyay.organizer.core.attendee.history.CheckInHistoryViewModel;
import com.eventyay.organizer.core.attendee.list.AttendeesViewModel;
import com.eventyay.organizer.core.attendee.qrscan.ScanQRViewModel;
import com.eventyay.organizer.core.auth.login.LoginViewModel;
import com.eventyay.organizer.core.auth.reset.ResetPasswordViewModel;
import com.eventyay.organizer.core.auth.signup.SignUpViewModel;
import com.eventyay.organizer.core.auth.start.StartViewModel;
import com.eventyay.organizer.core.event.about.AboutEventViewModel;
import com.eventyay.organizer.core.event.chart.ChartViewModel;
import com.eventyay.organizer.core.event.copyright.CreateCopyrightViewModel;
import com.eventyay.organizer.core.event.copyright.update.UpdateCopyrightViewModel;
import com.eventyay.organizer.core.event.create.CreateEventViewModel;
import com.eventyay.organizer.core.event.list.EventsViewModel;
import com.eventyay.organizer.core.event.list.sales.SalesSummaryViewModel;
import com.eventyay.organizer.core.faq.create.CreateFaqViewModel;
import com.eventyay.organizer.core.faq.list.FaqListViewModel;
import com.eventyay.organizer.core.feedback.list.FeedbackListViewModel;
import com.eventyay.organizer.core.main.EventViewModel;
import com.eventyay.organizer.core.main.OrganizerViewModel;
import com.eventyay.organizer.core.notification.list.NotificationsViewModel;
import com.eventyay.organizer.core.orders.create.CreateOrderViewModel;
import com.eventyay.organizer.core.orders.detail.OrderDetailViewModel;
import com.eventyay.organizer.core.orders.list.OrdersViewModel;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailViewModel;
import com.eventyay.organizer.core.organizer.password.ChangePasswordViewModel;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoViewModel;
import com.eventyay.organizer.core.role.invite.RoleInviteViewModel;
import com.eventyay.organizer.core.role.list.RoleListViewModel;
import com.eventyay.organizer.core.role.update.UpdateRoleViewModel;
import com.eventyay.organizer.core.session.create.CreateSessionViewModel;
import com.eventyay.organizer.core.settings.autocheckin.AutoCheckInViewModel;
import com.eventyay.organizer.core.settings.restriction.TicketSettingsViewModel;
import com.eventyay.organizer.core.speaker.details.SpeakerDetailsViewModel;
import com.eventyay.organizer.core.speakerscall.create.CreateSpeakersCallViewModel;
import com.eventyay.organizer.core.sponsor.create.CreateSponsorViewModel;
import com.eventyay.organizer.core.ticket.create.CreateTicketViewModel;
import com.eventyay.organizer.core.ticket.detail.TicketDetailViewModel;
import com.eventyay.organizer.core.track.create.CreateTrackViewModel;
import com.eventyay.organizer.core.track.update.UpdateTrackViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel.class)
    public abstract ViewModel bindStartViewModel(StartViewModel startViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel.class)
    public abstract ViewModel bindResetPasswordViewModel(ResetPasswordViewModel resetPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutEventViewModel.class)
    public abstract ViewModel bindAboutEventViewModel(AboutEventViewModel aboutEventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChartViewModel.class)
    public abstract ViewModel bindChartViewModel(ChartViewModel chartViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SpeakerDetailsViewModel.class)
    public abstract ViewModel bindSpeakerDetailsViewModel(SpeakerDetailsViewModel speakerDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrdersViewModel.class)
    public abstract ViewModel bindOrdersViewModel(OrdersViewModel ordersViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateOrderViewModel.class)
    public abstract ViewModel bindCreateOrderViewModel(CreateOrderViewModel createOrderViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateSpeakersCallViewModel.class)
    public abstract ViewModel bindCreateSpeakersCallViewModel(CreateSpeakersCallViewModel createSpeakersCallViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailViewModel.class)
    public abstract ViewModel bindOrderDetailViewModel(OrderDetailViewModel orderDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrganizerViewModel.class)
    public abstract ViewModel bindOrganizerViewModel(OrganizerViewModel organizerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    public abstract ViewModel bindEventViewModel(EventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel.class)
    public abstract ViewModel bindEventsViewModel(EventsViewModel eventsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateEventViewModel.class)
    public abstract ViewModel bindCreateEventViewModel(CreateEventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateCopyrightViewModel.class)
    public abstract ViewModel bindCreateCopyrightViewModel(CreateCopyrightViewModel createCopyrightViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpdateCopyrightViewModel.class)
    public abstract ViewModel bindUpdateCopyrightViewModel(UpdateCopyrightViewModel updateCopyrightViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateFaqViewModel.class)
    public abstract ViewModel bindCreateFaqViewModel(CreateFaqViewModel faqViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FaqListViewModel.class)
    public abstract ViewModel bindFaqListViewModel(FaqListViewModel faqListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateSponsorViewModel.class)
    public abstract ViewModel bindCreateSponsorViewModel(CreateSponsorViewModel sponsorViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateTrackViewModel.class)
    public abstract ViewModel bindCreateTrackViewModel(CreateTrackViewModel createTrackViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpdateTrackViewModel.class)
    public abstract ViewModel bindUpdateTrackViewModel(UpdateTrackViewModel updateTrackViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateTicketViewModel.class)
    public abstract ViewModel bindCreateTicketViewModel(CreateTicketViewModel ticketViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel.class)
    public abstract ViewModel bindSignUpViewModel(SignUpViewModel signUpViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TicketSettingsViewModel.class)
    public abstract ViewModel bindTicketSettingsViewModel(TicketSettingsViewModel ticketSettingsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CheckInHistoryViewModel.class)
    public abstract ViewModel bindCheckInHistoryViewModel(CheckInHistoryViewModel checkInHistoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrganizerDetailViewModel.class)
    public abstract ViewModel bindOrganizerDetailViewModel(OrganizerDetailViewModel organizerDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpdateOrganizerInfoViewModel.class)
    public abstract ViewModel bindUpdateOrganizerViewModel(UpdateOrganizerInfoViewModel ticketSettingsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel.class)
    public abstract ViewModel bindChangePasswordViewModel(ChangePasswordViewModel changePasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TicketDetailViewModel.class)
    public abstract ViewModel bindTicketDetailViewModel(TicketDetailViewModel ticketDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AttendeeCheckInViewModel.class)
    public abstract ViewModel bindAttendeeCheckinViewModel(AttendeeCheckInViewModel attendeeCheckInViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AutoCheckInViewModel.class)
    public abstract ViewModel bindAutoCheckInViewModel(AutoCheckInViewModel autoCheckInViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SalesSummaryViewModel.class)
    public abstract ViewModel bindSalesSummaryViewModel(SalesSummaryViewModel salesSummaryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FeedbackListViewModel.class)
    public abstract ViewModel bindFeedbackListViewModel(FeedbackListViewModel feedbackListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RoleInviteViewModel.class)
    public abstract ViewModel bindRoleInvitesViewModel(RoleInviteViewModel roleInviteViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RoleListViewModel.class)
    public abstract ViewModel bindRoleListViewModel(RoleListViewModel roleListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpdateRoleViewModel.class)
    public abstract ViewModel bindUpdateRoleViewModel(UpdateRoleViewModel updateRoleViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel.class)
    public abstract ViewModel bindNotificationsViewModel(NotificationsViewModel notificationsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ScanQRViewModel.class)
    public abstract ViewModel bindScanQRViewModel(ScanQRViewModel scanQRViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AttendeesViewModel.class)
    public abstract ViewModel bindAttendeesViewModel(AttendeesViewModel attendeesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateSessionViewModel.class)
    public abstract ViewModel bindCreateSessionViewModel(CreateSessionViewModel createSessionViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(OrgaViewModelFactory factory);
}
