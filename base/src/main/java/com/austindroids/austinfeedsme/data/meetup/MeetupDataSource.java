package com.austindroids.austinfeedsme.data.meetup;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.Results;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * Created by daz on 8/26/16.
 */

public class MeetupDataSource implements EventsDataSource {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference eventsReference = database.getReference("events");

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Results>() {
                    @Override
                    public void onSuccess(Results results) {
                        final HashMap<String, Event> meetupEventMap = new HashMap<>();
                        for (Event event : results.getEvents()) {
                            meetupEventMap.put(event.getId(), event);
                        }
                        eventsReference.orderByChild("time").startAt((new Date().getTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Event firebaseEvent = postSnapshot.getValue(Event.class);
                                    meetupEventMap.remove(firebaseEvent.getId());
                                }

                                Timber.d("After cleaning we have this many events: %s", meetupEventMap.size());
                                if (meetupEventMap.size() != 0) {
                                    callback.onEventsLoaded(new ArrayList<>(meetupEventMap.values()));
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.e(databaseError.toException());
                            }
                        });

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });

    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {

    }
}