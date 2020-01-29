package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.about.AboutUsFragment;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;
import com.eventyay.organizer.core.attendee.history.CheckInHistoryFragment;
import com.eventyay.organizer.core.attendee.list.AttendeesFragment;
import com.eventyay.organizer.core.event.dashboard.EventDashboardFragment;
import com.eventyay.organizer.core.event.list.EventListFragment;
import com.eventyay.organizer.core.event.list.pager.ListPageFragment;
import com.eventyay.organizer.core.event.list.sales.SalesSummaryFragment;
import com.eventyay.organizer.core.faq.create.CreateFaqFragment;
import com.eventyay.organizer.core.faq.list.FaqListFragment;
import com.eventyay.organizer.core.feedback.list.FeedbackListFragment;
import com.eventyay.organizer.core.notification.list.NotificationsFragment;
import com.eventyay.organizer.core.orders.detail.OrderDetailFragment;
import com.eventyay.organizer.core.orders.list.OrdersFragment;
import com.eventyay.organizer.core.role.invite.RoleInviteFragment;
import com.eventyay.organizer.core.role.list.RoleListFragment;
import com.eventyay.organizer.core.role.update.UpdateRoleFragment;
import com.eventyay.organizer.core.settings.SettingsFragment;
import com.eventyay.organizer.core.settings.autocheckin.AutoCheckInFragment;
import com.eventyay.organizer.core.settings.restriction.CheckInRestrictions;
import com.eventyay.organizer.core.speaker.list.SpeakersFragment;
import com.eventyay.organizer.core.speakerscall.detail.SpeakersCallFragment;
import com.eventyay.organizer.core.sponsor.create.CreateSponsorFragment;
import com.eventyay.organizer.core.sponsor.list.SponsorsFragment;
import com.eventyay.organizer.core.ticket.create.CreateTicketFragment;
import com.eventyay.organizer.core.ticket.detail.TicketDetailFragment;
import com.eventyay.organizer.core.ticket.list.TicketsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
@SuppressWarnings("PMD.TooManyMethods")
public abstract class MainFragmentBuildersModule {

    // Event

    @ContributesAndroidInjector
    abstract EventDashboardFragment contributeEventDashboardFragment();

    @ContributesAndroidInjector
    abstract EventListFragment contributeEventListFragment();

    @ContributesAndroidInjector
    abstract ListPageFragment contributeEventListChildFragment();

    @ContributesAndroidInjector
    abstract SalesSummaryFragment contributeSalesSummaryFragment();

    // Attendee

    @ContributesAndroidInjector
    abstract AttendeeCheckInFragment contributeAttendeeCheckinFragment();

    @ContributesAndroidInjector
    abstract AttendeesFragment contributeAttendeeFragment();

    @ContributesAndroidInjector
    abstract CheckInHistoryFragment contributeCheckInHistoryFragment();

    // Ticket
    @ContributesAndroidInjector
    abstract TicketsFragment contributeTicketFragment();

    @ContributesAndroidInjector
    abstract TicketDetailFragment contributeTicketDetailFragment();

    @ContributesAndroidInjector
    abstract CreateTicketFragment contributeCreateTicketFragment();

    // Setting

    @ContributesAndroidInjector
    abstract SettingsFragment constributeSettingFragment();

    @ContributesAndroidInjector
    abstract CheckInRestrictions contributeCheckInRestrictions();

    @ContributesAndroidInjector
    abstract AutoCheckInFragment contributesAutoCheckInFragment();

    // FAQ

    @ContributesAndroidInjector
    abstract FaqListFragment contributeFaqListFragment();

    @ContributesAndroidInjector
    abstract CreateFaqFragment contributeCreateFaqFragment();

    //Feedback

    @ContributesAndroidInjector
    abstract FeedbackListFragment contributeFeedbackListFragment();

    // Role

    @ContributesAndroidInjector
    abstract RoleInviteFragment contributeRoleInviteFragment();

    @ContributesAndroidInjector
    abstract RoleListFragment contributeRoleListFragment();

    @ContributesAndroidInjector
    abstract UpdateRoleFragment contributeUpdateRoleFragment();

    // Sponsor

    @ContributesAndroidInjector
    abstract SponsorsFragment contributeSponsorsFragment();

    @ContributesAndroidInjector
    abstract CreateSponsorFragment contributeCreateSponsorFragment();

    // Speaker

    @ContributesAndroidInjector
    abstract SpeakersFragment contributeSpeakersFragment();

    //SpeakersCall

    @ContributesAndroidInjector
    abstract SpeakersCallFragment contributeSpeakersCallFragment();

    // Order

    @ContributesAndroidInjector
    abstract OrdersFragment contributeOrdersFragment();

    @ContributesAndroidInjector
    abstract OrderDetailFragment contributeOrderDetailFragment();

    // Notification

    @ContributesAndroidInjector
    abstract NotificationsFragment contributeNotificationsFragment();

    // About the app

    @ContributesAndroidInjector
    abstract AboutUsFragment contributeAboutUsFragment();
}
