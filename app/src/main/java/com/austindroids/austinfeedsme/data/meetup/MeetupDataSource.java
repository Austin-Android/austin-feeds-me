package com.austindroids.austinfeedsme.data.meetup;

import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.Results;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by daz on 8/26/16.
 */

public class MeetupDataSource implements EventsDataSource {
    private static final String TAG = "MeetupDataSource";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("events");

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        RxJavaCallAdapterFactory rxAdapter =
                RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        MeetupService meetupService = retrofit.create(MeetupService.class);
        Observable<Results> meetupObservable =
                meetupService.getOpenEvents();

        Subscription meetupSubscription = meetupObservable
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Results>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Results results) {
                        Log.d(TAG, "Event count from Meetup API: " + results.getEvents().size());

                        final List<Event> meetupEvents = results.getEvents();

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Event event = postSnapshot.getValue(Event.class);

                                    Iterator<Event> iter = meetupEvents.iterator();

                                    while (iter.hasNext()) {
                                        Event nextEvent = iter.next();
                                        if (event.getId() != null &&
                                                event.getId().equals(nextEvent.getId())) {
                                            iter.remove();
                                        }
                                    }
                                }

                                Log.d(TAG, "After cleaning we have this many events: " +
                                        meetupEvents.size());
                                if (meetupEvents.size() != 0) {
                                    callback.onEventsLoaded(meetupEvents);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {

    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {

    }
}