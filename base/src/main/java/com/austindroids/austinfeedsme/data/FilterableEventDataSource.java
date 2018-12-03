package com.austindroids.austinfeedsme.data;

public interface FilterableEventDataSource extends EventsDataSource {
    void getEvents(LoadEventsCallback callback, boolean futureEvents,
                   boolean foodOnly);
}
