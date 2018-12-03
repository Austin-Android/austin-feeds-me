package com.austindroids.austinfeedsme.data.firebase;


import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.FilterableEventDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import timber.log.Timber;

@Singleton
public class FirebaseEventsDataSource implements FilterableEventDataSource {
    private final CollectionReference collectionReference;

    @Inject
    public FirebaseEventsDataSource(CollectionReference fireBase) {
        this.collectionReference = fireBase;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {
        getEvents(callback, false, false);
    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {
        collectionReference.add(eventToSave);
        callback.onEventSaved(true);
    }

    @Override
    public void getEvents(LoadEventsCallback callback, boolean futureEvents, boolean foodOnly) {
        Query eventsQuery = getEventsQuery(futureEvents, foodOnly);
        eventsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Event event = snapshot.toObject(Event.class);
                        events.add(event);
                    }
                    callback.onEventsLoaded(events);
                } else {
                    callback.onError(task.getException().toString());
                    Timber.e(task.getException());
                }
            }
        });
    }

    private Query getEventsQuery(boolean futureEvents, boolean foodOnly) {
        Query eventsQuery = collectionReference;

        if (foodOnly && futureEvents) {
            eventsQuery =  collectionReference
                    .whereGreaterThan("time", new Date().getTime())
                    .whereEqualTo("food", true)
                    .orderBy("time");
        }

        if (futureEvents) {
            eventsQuery = collectionReference.whereGreaterThan("time", new Date().getTime())
                    .orderBy("time");
        }
        if (foodOnly) {
            eventsQuery = collectionReference.whereEqualTo("food", true)
                    .orderBy("time");
        }

        return eventsQuery;
    }
}
