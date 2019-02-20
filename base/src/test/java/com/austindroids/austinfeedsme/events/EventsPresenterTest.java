package com.austindroids.austinfeedsme.events;

import com.austindroids.austinfeedsme.common.events.EventsContract;
import com.austindroids.austinfeedsme.common.events.EventsPresenter;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by darrankelinske on 5/4/16.
 */
public class EventsPresenterTest {

    private static List<Event> EVENTS = new ArrayList<>();

    @Mock
    FilterableEventsRepository eventsRepository;
    @Mock
    EventsContract.View eventsView;

    private EventsPresenter eventsPresenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventsPresenter = new EventsPresenter(eventsRepository, eventsView);

        // Test data
        EVENTS.add(
                new Event("1", "Pizza Fest", "Pizza Everywhere", 33928672270000L,
                        "www.pizza.com", "pizza", true));

        EVENTS.add(
                new Event("2", "Beer", "Duff Everywhere", 33928672270777L,
                        "www.duffman.com", "beer", true));

        when(eventsRepository.getEventsRX(true, true)).thenReturn(Observable.just(EVENTS));
    }

    @Test
    public void loadAllEventsFromRepositoryAndLoadIntoView() {
        // Given an initialized EventsPresenter with initialized events
        // When loading of Events is requested
        eventsPresenter.loadEvents();

        verify(eventsRepository).getEventsRX(true, true);

        //Verify that view is given a list of one event from the presenter
        ArgumentCaptor<List> showEventsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventsView).showEvents(showEventsArgumentCaptor.capture());
        assertTrue(showEventsArgumentCaptor.getValue().size() == 2);
    }
}
