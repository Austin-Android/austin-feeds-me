package com.austindroids.austinfeedsme.data.meetup;

import android.content.Context;
import android.util.Log;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.Results;
import com.facebook.stetho.okhttp3.StethoInterceptor;
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

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
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
    private static final String CACHE_CONTROL = "Cache-Control";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference eventsReference = database.getReference("events");

    public MeetupDataSource() {}

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        RxJavaCallAdapterFactory rxAdapter =
                RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        MeetupService meetupService = retrofit.create(MeetupService.class);
        Observable<Results> meetupObservable =
                meetupService.getOpenEvents();

        Log.d(TAG, "Getting events from Meetup.");
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

    private static Cache provideCache (Context context)
    {
        Cache cache = null;
        try
        {
            cache = new Cache( new File(context.getCacheDir(), "http-cache" ),
                    10 * 1024 * 1024 ); // 10 MB
        }
        catch (Exception e)
        {
            Log.e( TAG, "Could not create Cache!" );
        }
        return cache;
    }

    public static Interceptor provideCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Response response = chain.proceed( chain.request() );

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge( 7, TimeUnit.MINUTES )
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString() )
                        .build();
            }
        };
    }
}