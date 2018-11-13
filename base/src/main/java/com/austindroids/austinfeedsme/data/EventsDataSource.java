package com.austindroids.austinfeedsme.data;

import java.util.List;

/**
 * Created by darrankelinske on 4/10/16.
 */
public interface EventsDataSource {

    interface LoadEventsCallback {
        void onEventsLoaded(List<Event> events);
        void onError(String error);
    }

    interface SaveEventCallback {
        void onEventSaved(boolean success);
        void onError(String error);
    }

    void getEvents(LoadEventsCallback callback, boolean onlyFood);
    void saveEvent(Event eventToSave, SaveEventCallback callback);
}
