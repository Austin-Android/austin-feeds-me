package com.austindroids.austinfeedsme.events;

import com.austindroids.austinfeedsme.data.EventsRepository;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by darrankelinske on 5/4/16.
 */
public class EventsPresenterTest {

    @Mock
    EventsRepository eventsRepository;
    @Mock
    EventsContract.View view;

    EventsPresenter eventsPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventsPresenter = new EventsPresenter(eventsRepository, view);
    }
}
