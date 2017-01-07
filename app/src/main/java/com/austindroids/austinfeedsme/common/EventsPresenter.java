package com.austindroids.austinfeedsme.common;

import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.events.EventsFilterType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by darrankelinske on 5/2/16.
 */
public class EventsPresenter implements EventsContract.Presenter {

    private EventsRepository repository;
    private EventsContract.View view;

    private EventsFilterType currentFiltering = EventsFilterType.ALL_EVENTS;

    @Inject
    public EventsPresenter(EventsRepository repository, EventsContract.View view) {
        this.view = view;
        this.repository = repository;

    }

    @Override
    public void loadEvents() {
        view.showProgress();
        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {

                Calendar currentDay = Calendar.getInstance();
                currentDay.set(Calendar.HOUR_OF_DAY, 23);
                currentDay.set(Calendar.MINUTE, 59);

                long currDateLong = currentDay.getTimeInMillis();

                Calendar sevenDaysFromNowCalendar = Calendar.getInstance();
                sevenDaysFromNowCalendar.add(Calendar.DATE, +7);

                long sevenDaysFromNow = sevenDaysFromNowCalendar.getTimeInMillis();

                ArrayList<Event> currentEvents = new ArrayList<>();

                for (Event nextEvent : events) {
                    if (nextEvent.isFood() &&
                            (nextEvent.getTime() > new Date().getTime())) {

                        if (nextEvent.getDescription().toLowerCase().contains("pizza")) {
                            nextEvent.setFoodType("pizza");
                        } else if (nextEvent.getDescription().toLowerCase().contains("beer")) {
                            nextEvent.setFoodType("beer");
                        } else if (nextEvent.getDescription().toLowerCase().contains("tacos")) {
                            nextEvent.setFoodType("tacos");
                        }else{
                            nextEvent.setFoodType("noFood");
                        }

                        switch (currentFiltering) {
                            case ALL_EVENTS:
                                currentEvents.add(nextEvent);
                                break;
                            case TODAYS_EVENTS:
                                if (nextEvent.getTime() < currDateLong) {
                                    currentEvents.add(nextEvent);
                                }
                                break;
                            case THIS_WEEKS_EVENTS:
                                if (nextEvent.getTime() < sevenDaysFromNow) {
                                    currentEvents.add(nextEvent);
                                }
                                break;
                            default:
                                currentEvents.add(nextEvent);
                                break;
                        }

                    }
                }

                Collections.sort(currentEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        return event1.getTime().compareTo(event2.getTime()); // Ascending
                    }
                });

                if (currentEvents.size() > 0 ) {
                    view.showEvents(currentEvents);
                } else {
                    view.showNoEventsView();
                }

                view.hideProgress();
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");
                view.hideProgress();
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
                    Event event = iter.next();

                    // Remove event if it doesn't have free food or is in the past
                    // or if the event name or description doesn't contain the search term
                    if (event.isFood()
                            && (event.getTime() > new Date().getTime()))
                    {
                        if (event.getDescription().toLowerCase().contains("pizza")) {
                            event.setFoodType("pizza");
                            Integer previousValue = yummyCounts.get("pizza");
                            yummyCounts.put("pizza", previousValue == null ? 1 : previousValue + 1);
                        }

                        if (event.getDescription().toLowerCase().contains("beer")) {
                            event.setFoodType("beer");
                            Integer previousValue = yummyCounts.get("beer");
                            yummyCounts.put("beer", previousValue == null ? 1 : previousValue + 1);
                        }

                        if (event.getDescription().toLowerCase().contains("taco")) {
                            event.setFoodType("tacos");
                            Integer previousValue = yummyCounts.get("tacos");
                            yummyCounts.put("tacos", previousValue == null ? 1 : previousValue + 1);
                        }

                        Integer previousValue = yummyCounts.get("total");
                        yummyCounts.put("total", previousValue == null ? 1 : previousValue + 1);
                    }
                }

                view.setPizzaCount(yummyCounts.get("pizza") == null ? 0 : yummyCounts.get("pizza"));
                view.setTacoCount(yummyCounts.get("tacos") == null ? 0 : yummyCounts.get("tacos"));
                view.setBeerCount(yummyCounts.get("beer") == null ? 0 : yummyCounts.get("beer"));
                view.setTotalCount(yummyCounts.get("total") == null ? 0 : yummyCounts.get("total"));
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

    @Override
    public void setFiltering(EventsFilterType requestType) {
        currentFiltering = requestType;
    }
}
