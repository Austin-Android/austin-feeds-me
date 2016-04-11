package com.example.utfeedsme.data;

import java.util.List;

/**
 * Created by darrankelinske on 4/10/16.
 */
public interface EventsDataSource {

    interface LoadEventsCallback {

        void onEventsLoaded(List<Event> events);

        void onError(String error);
    }

    interface LoadEventCallback {

        void onEventLoaded(Event event);

        void onError(String error);

    }

    void getEvents(LoadEventsCallback callback);

    void getEvent(String eventId, LoadEventCallback callback);
}
