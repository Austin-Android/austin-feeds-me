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

import hugo.weaving.DebugLog;

import static com.austindroids.austinfeedsme.data.Event.Type.BEER;
import static com.austindroids.austinfeedsme.data.Event.Type.PIZZA;
import static com.austindroids.austinfeedsme.data.Event.Type.TACO;

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

            @DebugLog
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
                    view.setTotalCount(currentEvents.size());
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
            @DebugLog
            @Override
            public void onEventsLoaded(List<Event> events) {

                Iterator<Event> iterator = events.iterator();

                while (iterator.hasNext()) {
                    Event event = iterator.next();

                    // Remove event if it doesn't have free food or is in the past
                    // or if the event name or description doesn't contain the search term
                    if (event.isFood() && event.getFoodType() != null
                            && (event.getTime() > new Date().getTime()))
                    {
                        if (event.getFoodType().equals(PIZZA.name())) {
                            Integer previousValue = yummyCounts.get(PIZZA.name());
                            yummyCounts.put(PIZZA.name(), previousValue == null ? 1 : previousValue + 1);
                        }
                        if (event.getFoodType().equals(BEER.name())) {
                            Integer previousValue = yummyCounts.get(BEER.name());
                            yummyCounts.put(BEER.name(), previousValue == null ? 1 : previousValue + 1);
                        }
                        if (event.getFoodType().equals(TACO.name())) {
                            Integer previousValue = yummyCounts.get(TACO.name());
                            yummyCounts.put(TACO.name(), previousValue == null ? 1 : previousValue + 1);
                        }
                    }
                }

                view.setPizzaCount(yummyCounts.get(PIZZA.name()) == null ? 0 : yummyCounts.get(PIZZA.name()));
                view.setTacoCount(yummyCounts.get(TACO.name()) == null ? 0 : yummyCounts.get(TACO.name()));
                view.setBeerCount(yummyCounts.get(BEER.name()) == null ? 0 : yummyCounts.get(BEER.name()));
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
