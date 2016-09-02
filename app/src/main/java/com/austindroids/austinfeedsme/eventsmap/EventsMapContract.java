package com.austindroids.austinfeedsme.eventsmap;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.events.EventsFilterType;

import java.util.List;

/**
 * Created by daz on 8/5/16.
 */
public class EventsMapContract {

    interface Presenter{
        void loadEvents();
        void showFiltering();
        void setFiltering(EventsFilterType eventsFilterType);
    }
    interface View{
        void showEvents(List<Event> events);
        void showFilteringPopUpMenu();
    }

}
