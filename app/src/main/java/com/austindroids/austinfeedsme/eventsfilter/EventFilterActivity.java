package com.austindroids.austinfeedsme.eventsfilter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.Results;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEventsDateSource;
import com.austindroids.austinfeedsme.data.meetup.MeetupService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
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
import rx.subscriptions.CompositeSubscription;


/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends AppCompatActivity implements EventFilterContract.View {

    private static final String TAG ="EventFilterActivity";

    private RecyclerView eventsRecyclerView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("events");
    final EventFilterAdapter eventFilterAdapter = new EventFilterAdapter(new ArrayList<Event>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);

        myRef.keepSynced(true);

        eventsRecyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventFilterAdapter);

        //cleanEvents();

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

                        final List<Event> meetupEventsFromTheFuture = results.getEvents();

                        Iterator<Event> iterToGetEventsInFuture = meetupEventsFromTheFuture.iterator();

                        while (iterToGetEventsInFuture.hasNext()) {
                            Event nextEvent = iterToGetEventsInFuture.next();
                            if (nextEvent.getTime() < new Date().getTime()) {
                                iterToGetEventsInFuture.remove();
                            }
                        }
                        Log.d(TAG, "Event count after cleaning past events: " +
                                meetupEventsFromTheFuture.size());

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Event event = postSnapshot.getValue(Event.class);

                                    Iterator<Event> iter = meetupEventsFromTheFuture.iterator();

                                    while (iter.hasNext()) {
                                        Event nextEvent = iter.next();
                                        if (event.getId() != null &&
                                                event.getId().equals(nextEvent.getId())) {
                                            iter.remove();
                                        }
                                    }
                                }

                                Log.d(TAG, "After cleaning we have this many events: " +
                                        meetupEventsFromTheFuture.size());
                                if (meetupEventsFromTheFuture.size() != 0) {
                                    eventFilterAdapter.addEvents(meetupEventsFromTheFuture);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

        compositeSubscription.add(meetupSubscription);

        EventsRepository eventsRepository  = new EventsRepository(new EventbriteEventsDateSource());
        EventFilterPresenter eventFilterPresenter = new EventFilterPresenter(eventsRepository, this);


    }



    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }

    public void cleanEvents() {
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
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void showEvents(List<Event> events) {

    }
}
