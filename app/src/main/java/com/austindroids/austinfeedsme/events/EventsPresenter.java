package com.austindroids.austinfeedsme.events;

import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by darrankelinske on 5/2/16.
 */
public class EventsPresenter implements EventsContract.UserActionsListener {


    private EventsRepository repository;
    private EventsContract.View view;

    public EventsPresenter(EventsRepository repository, EventsContract.View view) {
        this.view = view;
        this.repository = repository;

    }

    @Override
    public void loadEvents() {
        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {


                ArrayList<Event> currentEvents = new ArrayList<>();

                for (Event nextEvent : events) {
                    if (nextEvent.isFood() &&
                            (nextEvent.getTime() > new Date().getTime())) {
                        currentEvents.add(nextEvent);
                    }
                }

                Collections.sort(currentEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        return event1.getTime().compareTo(event2.getTime()); // Ascending
                    }
                });

                view.showEvents(currentEvents);
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });
    }

    @Override
    public void loadYummyCounts() {

        final HashMap<String, Integer> yummyCounts = new HashMap<>();


        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {

                Iterator<Event> iter = events.iterator();

                while (iter.hasNext()) {
                    Event nextEvent = iter.next();

                    // Remove event if it doesn't have free food or is in the past
                    // or if the event name or description doesn't contain the search term
                    if (nextEvent.isFood()
                            && (nextEvent.getTime() > new Date().getTime()))
                    {
                        if (nextEvent.getDescription().toLowerCase().contains("pizza")) {
                            Integer previousValue = yummyCounts.get("pizza");
                            yummyCounts.put("pizza", previousValue == null ? 1 : previousValue + 1);
                        } else if (nextEvent.getDescription().toLowerCase().contains("beer")) {
                            Integer previousValue = yummyCounts.get("beer");
                            yummyCounts.put("beer", previousValue == null ? 1 : previousValue + 1);
                        } else if (nextEvent.getDescription().toLowerCase().contains("tacos")) {
                            Integer previousValue = yummyCounts.get("tacos");
                            yummyCounts.put("tacos", previousValue == null ? 1 : previousValue + 1);
                        }
                    }
                }

                view.setPizzaCount(yummyCounts.get("pizza") == null ? 0 : yummyCounts.get("pizza"));
                view.setTacoCount(yummyCounts.get("tacos") == null ? 0 : yummyCounts.get("tacos"));
                view.setBeerCount(yummyCounts.get("beer") == null ? 0 : yummyCounts.get("beer"));
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });

    }

    @Override
    public void searchEvents(final String searchTerm) {

        if (searchTerm.equalsIgnoreCase("reset")) {
            loadEvents();
            return;
        }

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
                            || !nextEvent.getDescription().toLowerCase().contains(lowerCaseSearch)) {
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
