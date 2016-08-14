package com.austindroids.austinfeedsme.events;

import com.austindroids.austinfeedsme.data.Event;

import java.util.List;

/**
 * Created by darrankelinske on 5/2/16.
 */
interface EventsContract {

    interface View {

        void showEvents(List<Event> events);

        void setPizzaCount(int count);

        void setTacoCount(int count);

        void setBeerCount(int count);

    }

    interface UserActionsListener {

        void loadEvents();

        void searchEvents(String searchTerm);

        void openEventDetails(Event clickedEvent);

    }

}
