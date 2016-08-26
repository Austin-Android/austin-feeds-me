package com.austindroids.austinfeedsme.data.eventbrite;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.utility.TypeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by darrankelinske on 8/26/16.
 */
public class EventbriteEventsDateSource implements EventsDataSource {

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        RxJavaCallAdapterFactory rxAdapter =
                RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit eventbriteRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.eventbriteapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        EventbriteService eventbriteService = eventbriteRetrofit.create(EventbriteService.class);

        String[] searchList = new String[]{"taco","pizza", "beer", "breakfast", "lunch", "dinner",
                "drinks", "spaghetti", "hamburger"};

        List<Observable<EventbriteEvents>> observableList = new ArrayList<>();

        for (String searchTerm : searchList) {
            observableList.add(eventbriteService.getEventsByKeyword(searchTerm));
        }

        Subscriber<EventbriteEvents> eventbriteEventsSubscriber = new Subscriber<EventbriteEvents>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                // cast to retrofit.HttpException to get the response code
                if (e instanceof HttpException) {
                    HttpException response = (HttpException)e;
                    int code = response.code();
                }

            }

            @Override
            public void onNext(EventbriteEvents eventbriteEvents) {

                final List<Event> convertedEventbriteEvents = new ArrayList<Event>();
                for (EventbriteEvent eventbriteEvent : eventbriteEvents.getEvents()) {
                    convertedEventbriteEvents.add(TypeUtils.transformEventBrite(eventbriteEvent));
                }

                callback.onEventsLoaded(convertedEventbriteEvents);
            }
        };

        Subscription eventbriteSubscription = Observable.merge(observableList)
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventbriteEventsSubscriber);

    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {

    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {

    }
}
