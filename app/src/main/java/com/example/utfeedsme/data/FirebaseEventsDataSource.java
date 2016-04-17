package com.example.utfeedsme.data;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrankelinske on 4/10/16.
 */
public class FirebaseEventsDataSource implements EventsDataSource {

    private static FirebaseEventsDataSource INSTANCE;


    public static FirebaseEventsDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseEventsDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private FirebaseEventsDataSource() {}

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        final List<Event> events = new ArrayList<Event>();

        Firebase ref = new Firebase("https://austin-feeds-me.firebaseio.com/events");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        Firebase ref = new Firebase("https://austin-feeds-me.firebaseio.com/events");
        Firebase newEventRef = ref.push();

        newEventRef.setValue(eventToSave);

        Log.d("Woo", "The new event ID is: "+newEventRef.getKey());
     }

}
