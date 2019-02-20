package com.austindroids.austinfeedsme.eventsfilter;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;
import com.austindroids.austinfeedsme.di.modules.DataModule.Eventbrite;
import com.austindroids.austinfeedsme.di.modules.DataModule.Meetup;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterPresenter implements EventFilterContract.Presenter {

    private FilterableEventsRepository filterableEventsRepository;
    private EventsRepository eventbriteRepository;
    private EventsRepository meetupRepository;
    private EventFilterContract.View view;

    @Inject
    public EventFilterPresenter(FilterableEventsRepository filterableEventsRepository, @Eventbrite EventsRepository eventbriteRepository,
                                @Meetup EventsRepository meetupRepository, EventFilterContract.View view) {
        this.filterableEventsRepository = filterableEventsRepository;
        this.eventbriteRepository = eventbriteRepository;
        this.meetupRepository = meetupRepository;
        this.view = view;
    }

    @Override
    public void loadEvents() {

        Observable<List<Event>> mergedIncomingEvents = eventbriteRepository.getEventsRX();

        Observable.combineLatest(filterableEventsRepository.getEventsRX(true, false), mergedIncomingEvents,
                (BiFunction<List<Event>, List<Event>, List<Event>>) (persistedEvents, newEvents) -> {
                    Set<String> persistedIds = new HashSet<>();

                    for (Event event : persistedEvents) {
                        persistedIds.add(event.getId());
                    }

                    Iterator<Event> eventIterator = newEvents.iterator();

                    while (eventIterator.hasNext()) {
                        Event currentEvent = eventIterator.next();
                        if (persistedEvents.contains(currentEvent.getId())) {
                            eventIterator.remove();
                        }
                    }
                    return newEvents;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                sortEvents(events);
                view.showEvents(events);
            }
        });
    }

    private void sortEvents(List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if (o1.getTime() < o2.getTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
}