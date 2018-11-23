package com.austindroids.austinfeedsme.eventsfilter;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;

import java.util.List;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterPresenter implements EventFilterContract.Presenter {

    private EventsDataSource eventbriteRepository;
    private EventsDataSource meetupRepository;
    private EventFilterContract.View view;

    public EventFilterPresenter(EventsDataSource eventsDataSource,
                                EventsDataSource meetupDataSource, EventFilterContract.View view) {
        this.eventbriteRepository = eventsDataSource;
        this.meetupRepository = meetupDataSource;
        this.view = view;
    }

    @Override
    public void loadEvents() {
        eventbriteRepository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {

            }
        }, false);

        meetupRepository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }
}
