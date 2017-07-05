package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.event.detail.EventDetailPresenter;
import org.fossasia.openevent.app.event.detail.TicketAnalyser;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IEventDetailView eventDetailView;

    @Mock
    IEventRepository eventRepository;

    @Mock
    TicketAnalyser ticketAnalyser;

    private final int id = 42;
    private EventDetailPresenter eventDetailPresenter;

    private Event event = new Event(id);

    private List<Attendee> attendees = Arrays.asList(
        new Attendee(false),
        new Attendee(true),
        new Attendee(false),
        new Attendee(false),
        new Attendee(true),
        new Attendee(true),
        new Attendee(false)
    );

    private List<Ticket> tickets = Arrays.asList(
        new Ticket(1, 21),
        new Ticket(2, 50),
        new Ticket(3, 43));

    @Before
    public void setUp() {
        // Event set up
        event.setName("Event Name");
        event.setStartTime("2004-05-21T9:30:00");
        event.setEndTime("2012-09-20T12:23:00");
        event.setTickets(tickets);

        eventDetailPresenter = new EventDetailPresenter(eventRepository, ticketAnalyser);
        eventDetailPresenter.attach(eventDetailView, event.getId());

        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventAndAttendeesAutomatically() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        verify(eventRepository).getEvent(id, false);
        verify(eventRepository).getAttendees(id, false);
    }

    @Test
    public void shouldDetachViewOnStop() {
        assertNotNull(eventDetailPresenter.getView());

        eventDetailPresenter.detach();

        eventDetailPresenter.start();
        eventDetailPresenter.loadDetails(true);

        assertNull(eventDetailPresenter.getView());
    }

    @Test
    public void shouldShowEventError() {
        String error = "Test Error";
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showError(error);
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showEvent(event);
        verify(ticketAnalyser).analyseTotalTickets(event);
    }

    @Test
    public void shouldShowAttendeeError() {
        String error = "Test Error";
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable(error)));

        eventDetailPresenter.loadDetails(false);

        verify(eventDetailView).showError(error);
    }

    @Test
    public void shouldLoadAttendeesSuccessfully() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        eventDetailPresenter.start();

        verify(ticketAnalyser).analyseSoldTickets(event, attendees);
    }

    @Test
    public void shouldNotAccessView() {
        eventDetailPresenter.detach();

        eventDetailPresenter.loadDetails(false);

        verifyZeroInteractions(eventDetailView);
    }

    @Test
    public void shouldHideProgressbarCorrectly() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldHideProgressbarOnEventError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.error(new Throwable()));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldHideProgressbarOnAttendeeError() {
        when(eventRepository.getAttendees(id, false))
            .thenReturn(Observable.error(new Throwable()));

        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldHideProgressbarOnCompleteError() {
        when(eventRepository.getEvent(id, false))
            .thenReturn(Observable.error(new Throwable()));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.start();

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
    }

    @Test
    public void shouldHideRefreshLayoutCorrectly() {
        when(eventRepository.getAttendees(id, true))
            .thenReturn(Observable.fromIterable(attendees));

        when(eventRepository.getEvent(id, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
        inOrder.verify(eventDetailView).onRefreshComplete();
    }

    @Test
    public void shouldHideRefreshLayoutOnEventError() {
        when(eventRepository.getEvent(id, true))
            .thenReturn(Observable.error(new Throwable()));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
        inOrder.verify(eventDetailView).onRefreshComplete();
    }

    @Test
    public void shouldHideRefreshLayoutOnAttendeeError() {
        when(eventRepository.getAttendees(id, true))
            .thenReturn(Observable.error(new Throwable()));

        when(eventRepository.getEvent(id, true))
            .thenReturn(Observable.just(event));

        InOrder inOrder = Mockito.inOrder(eventDetailView);

        eventDetailPresenter.loadDetails(true);

        inOrder.verify(eventDetailView).showProgressBar(true);
        inOrder.verify(eventDetailView).showProgressBar(false);
        inOrder.verify(eventDetailView).onRefreshComplete();
    }

}
