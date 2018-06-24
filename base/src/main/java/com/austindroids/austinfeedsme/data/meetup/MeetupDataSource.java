package com.austindroids.austinfeedsme.data.meetup;

import android.content.Context;
import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.Results;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * Created by daz on 8/26/16.
 */

public class MeetupDataSource implements EventsDataSource {
    private static final String TAG = "MeetupDataSource";
    private static final String CACHE_CONTROL = "Cache-Control";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference eventsReference = database.getReference("events");

    public MeetupDataSource() {}

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        RxJava2CallAdapterFactory rxAdapter =
                RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        MeetupService meetupService = retrofit.create(MeetupService.class);

        meetupService.getOpenEvents()
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Results>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Results results) {
                        Log.d(TAG, "Event count from Meetup API: " + results.getEvents().size());

                        final List<Event> meetupEvents = results.getEvents();
                        eventsReference.orderByChild("time").startAt((new Date().getTime()));
                        eventsReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                Timber.d("After cleaning we have this many events: %s", meetupEvents.size());
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