package com.austindroids.austinfeedsme.events;

import android.app.Activity;
import android.util.Log;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by darrankelinske on 5/2/16.
 */
public class EventsPresenter implements EventsContract.UserActionsListener {

    @Inject
    DatabaseReference firebase;
    @Inject
    EventsRepository repository;

    EventsContract.View view;

    public EventsPresenter(EventsContract.View view, Activity activity) {
        this.view = view;
        ((AustinFeedsMeApplication) activity.getApplication()).component().inject(this);
    }

    @Override
    public void loadEvents() {
        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {

                Iterator<Event> iter = events.iterator();

                while (iter.hasNext()) {
                    Event nextEvent = iter.next();
                    if (!nextEvent.isFood() ||
                            (nextEvent.getTime() < new Date().getTime())) {
                        iter.remove();
                    }
                }

                view.showEvents(events);
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });

    }

    @Override
    public void searchEvents(final String searchTerm) {

        // Probably better to use Regex
        // http://stackoverflow.com/questions/14018478/string-contains-ignore-case
        final String lowerCaseSearch = searchTerm.toLowerCase();

        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {

                Iterator<Event> iter = events.iterator();

                while (iter.hasNext()) {
                    Event nextEvent = iter.next();

                    // Remove event if it doesn't have free food or is in the past
                    // or if the event name or description doesn't contain the search term
                    if (!nextEvent.isFood()
                            || (nextEvent.getTime() < new Date().getTime())
                            || !(nextEvent.getName().toLowerCase().contains(lowerCaseSearch)
                              || nextEvent.getDescription().toLowerCase().contains(lowerCaseSearch))) {
                        iter.remove();
                    }
                }

                view.showEvents(events);
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });

    }

    @Override
    public void openEventDetails(Event clickedEvent) {

    }
}
