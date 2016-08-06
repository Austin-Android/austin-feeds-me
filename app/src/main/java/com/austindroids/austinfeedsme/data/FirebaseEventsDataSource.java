package com.austindroids.austinfeedsme.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseEventsDataSource implements EventsDataSource {
    private final DatabaseReference fireBase;

    @Inject
    public FirebaseEventsDataSource(DatabaseReference fireBase) {
        this.fireBase = fireBase;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {
        final List<Event> events = new ArrayList<Event>();
        fireBase.orderByChild("time").startAt((new Date().getTime()));
        fireBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    events.add(event);
                }
                callback.onEventsLoaded(events);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {

    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {
        DatabaseReference newEventRef = fireBase.push();

        newEventRef.setValue(eventToSave);

        Log.d("Woo", "The new event ID is: " + newEventRef.getKey());

        callback.onEventSaved(true);
     }

}
