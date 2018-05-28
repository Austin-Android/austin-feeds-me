package com.austindroids.austinfeedsme.eventsfilter;

import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterPresenter implements EventFilterContract.Presenter {

    private static final String TAG ="EventFilterPresenter";

    private EventsDataSource eventbriteRepository;
    private EventsDataSource meetupRepository;
    private EventFilterContract.View view;

    public EventFilterPresenter(EventsDataSource eventsDataSource,
                                EventsDataSource meetupDataSource, EventFilterContract.View view) {
        this.eventbriteRepository = eventsDataSource;
        this.meetupRepository = meetupDataSource;
        this.view = view;
    }

    @Override
    public void loadEvents() {

        cleanPastEvents();

        eventbriteRepository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {

            }
        });

        meetupRepository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    public void cleanPastEvents() {
        final List<Event> events = new ArrayList<Event>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("events");
        myRef.orderByChild("time");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if(event.getTime() < (new Date().getTime() - 2678400000L)) {
                        Log.i(TAG, "this event could be cleaned from firebase" + snapshot.getRef());
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

}
