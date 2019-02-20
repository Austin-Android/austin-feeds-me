package com.austindroids.austinfeedsme.data;

public interface FilterableEventDataSource extends EventsDataSource {
    void getEvents(LoadEventsCallback callback, boolean futureEvents,
                   boolean foodOnly);

    // This will retrieve all events (including those in the past and those that do not have food)
    void getAllEvents(LoadEventsCallback callback);
}
