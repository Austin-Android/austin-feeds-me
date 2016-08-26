package com.austindroids.austinfeedsme.eventsfilter;

import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterPresenter implements EventFilterContract.Presenter {

    private static final String TAG ="EventFilterPresenter";

    private EventsDataSource eventsRepository;
    private EventFilterContract.View view;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("events");

    public EventFilterPresenter(EventsDataSource eventsDataSource, EventFilterContract.View view) {
        this.eventsRepository = eventsDataSource;
        this.view = view;
    }

    @Override
    public void loadEvents() {


        eventsRepository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                cleanAndLoadEventbriteEvents(events);
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {

            }
        });



    }

    private void cleanAndLoadEventbriteEvents(final List<Event> events) {
        final Long callbackTimestamp = new Date().getTime();
        Log.d(TAG, "onResponse: Event's from eventbrite " + callbackTimestamp + ":"
                +events.size());


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);

                    Iterator<Event> iter = events.iterator();

                    while (iter.hasNext()) {
                        Event nextEvent = iter.next();
                        if (event.getId() != null &&
                                event.getId().equals(nextEvent.getId())) {
                            iter.remove();
                        }
                    }
                }

                Log.d(TAG, "onResponse: Event's from after cleaning " + callbackTimestamp + ":"
                        +events.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
