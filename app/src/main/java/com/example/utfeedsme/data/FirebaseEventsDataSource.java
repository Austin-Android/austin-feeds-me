package com.example.utfeedsme.data;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseEventsDataSource implements EventsDataSource {
    private final Firebase fireBase;

    @Inject
    public FirebaseEventsDataSource(Firebase fireBase) {
        this.fireBase = fireBase;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {
        final List<Event> events = new ArrayList<Event>();

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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {

    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {
        fireBase.push();

        fireBase.setValue(eventToSave);

        Log.d("Woo", "The new event ID is: " + fireBase.getKey());
     }

}
