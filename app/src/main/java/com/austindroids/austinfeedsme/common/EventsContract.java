package com.austindroids.austinfeedsme.common;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.events.EventsFilterType;

import java.util.List;

/**
 * Created by darrankelinske on 5/2/16.
 */
public interface EventsContract {

    interface View {
        void showEvents(List<Event> events);
        void setPizzaCount(int count);
        void setTacoCount(int count);
        void setBeerCount(int count);
        void setTotalCount(int count);
        void showFilteringPopUpMenu();
        void showNoEventsView();
        void showProgress();
        void hideProgress();
    }

    interface Presenter {
        void loadEvents();
        void loadYummyCounts();
        void searchEvents(String searchTerm);
        void openEventDetails(Event clickedEvent);
        void setFiltering(EventsFilterType requestType);
    }
}
