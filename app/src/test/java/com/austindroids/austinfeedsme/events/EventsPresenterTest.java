package com.austindroids.austinfeedsme.events;

import com.austindroids.austinfeedsme.common.EventsPresenter;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by darrankelinske on 5/4/16.
 */
public class EventsPresenterTest {

    private static List<Event> EVENTS = new ArrayList<>();

    @Mock
    EventsRepository eventsRepository;
    @Mock
    EventsContract.View eventsView;

    private EventsPresenter eventsPresenter;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<EventsDataSource.LoadEventsCallback> loadEventsCallbackCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventsPresenter = new EventsPresenter(eventsRepository, eventsView);

        // Test data
        EVENTS.add(
                new Event("1", "Pizza Fest", "Pizza Everywhere", 33928672270000L,
                        "www.pizza.com", true));

        System.out.println("setUp: Events size is: " +EVENTS.size());
    }

    @Test
    public void loadAllEventsFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized events
        // When loading of Events is requested
        eventsPresenter.loadEvents();

        // Callback is captured and invoked with stubbed tasks
        verify(eventsRepository).getEvents(loadEventsCallbackCaptor.capture());
        loadEventsCallbackCaptor.getValue().onEventsLoaded(EVENTS);

        ArgumentCaptor<List> showEventsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventsView).showEvents(showEventsArgumentCaptor.capture());
        assertTrue(showEventsArgumentCaptor.getValue().size() == 1);
    }
}
