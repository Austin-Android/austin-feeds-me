package com.austindroids.austinfeedsme.eventsmap;

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
import java.util.List;

/**
 * Created by daz on 8/5/16.
 */
public class EventsMapPresenter implements EventsMapContract.Presenter{
    private EventsRepository repository;
    private EventsMapContract.View view;
    private EventsFilterType currentFiltering = EventsFilterType.ALL_EVENTS;

    public EventsMapPresenter(EventsRepository repository, EventsMapContract.View view) {
        this.repository = repository;
        this.view = view;
    }

    public void loadEvents() {
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
                    if (nextEvent.isFood() && nextEvent.getVenue() != null &&
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

            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });
    }

    @Override
    public void showFiltering() {
        view.showFilteringPopUpMenu();
    }

    @Override
    public void setFiltering(EventsFilterType requestType) {
        currentFiltering = requestType;
    }
}
