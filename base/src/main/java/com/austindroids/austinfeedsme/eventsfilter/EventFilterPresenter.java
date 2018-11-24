package com.austindroids.austinfeedsme.eventsfilter;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterPresenter implements EventFilterContract.Presenter {

    private EventsRepository eventsRepository;
    private EventsRepository eventbriteRepository;
    private EventsRepository meetupRepository;
    private EventFilterContract.View view;

    public EventFilterPresenter(EventsRepository eventsRepository, EventsRepository eventbriteRepository,
                                EventsRepository meetupRepository, EventFilterContract.View view) {
        this.eventsRepository = eventsRepository;
        this.eventbriteRepository = eventbriteRepository;
        this.meetupRepository = meetupRepository;
        this.view = view;
    }

    @Override
    public void loadEvents() {

        Observable<List<Event>> mergedIncomingEvents = Observable.merge(eventbriteRepository.getEventsRX(false),
                meetupRepository.getEventsRX(false));

        Observable.combineLatest(eventsRepository.getEventsRX(false), mergedIncomingEvents,
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
                }).subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                sortEvents(events);
                view.showEvents(events);
            }
        });
    }

//    private void removeDuplicateEvents(List<Event> events) {
//        Set<String> eventIds = new HashSet<>();
//        Iterator<Event> eventIterator = events.iterator();
//
//        while (eventIterator.hasNext()) {
//            Event currentEvent = eventIterator.next();
//            if (!eventIds.contains(currentEvent.getId())) {
//                eventIds.add(currentEvent.getId());
//            } else {
//                eventIterator.remove();
//            }
//        }
//    }

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
