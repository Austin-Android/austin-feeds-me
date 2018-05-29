package com.austindroids.austinfeedsme.eventsfilter;

import com.austindroids.austinfeedsme.data.Event;

import java.util.List;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterContract {

    interface View {
        void showEvents(List<Event> events);
    }

    interface Presenter {
        void loadEvents();
    }

}
