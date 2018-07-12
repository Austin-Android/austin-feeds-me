package com.austindroids.austinfeedsme.data.firebase;

import android.support.annotation.NonNull;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class FirebaseEventsDataSource implements EventsDataSource {
    private final CollectionReference collectionReference;

    @Inject
    public FirebaseEventsDataSource(CollectionReference fireBase) {
        this.collectionReference = fireBase;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {
        final List<Event> events = new ArrayList<Event>();
        collectionReference
                .whereEqualTo("food", true)
                .whereGreaterThan("time", new Date().getTime())
                .orderBy("time")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Event event = snapshot.toObject(Event.class);
                        events.add(event);
                        callback.onEventsLoaded(events);
                    }
                } else {
                    callback.onError(task.getException().toString());
                    Timber.e(task.getException());
                }
            }
        });
    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {
        collectionReference.add(eventToSave);
        callback.onEventSaved(true);
    }
}
