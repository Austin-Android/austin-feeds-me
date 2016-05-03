package com.example.utfeedsme.events;

import com.example.utfeedsme.data.Event;

import java.util.List;

/**
 * Created by darrankelinske on 5/2/16.
 */
interface EventsContract {

    interface View {

        void showEvents(List<Event> events);

    }

    interface UserActionsListener {

        void loadEvents();

        void openEventDetails(Event clickedEvent);

    }

}
