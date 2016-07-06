package com.austindroids.austinfeedsme.events;

import android.app.Activity;
import android.util.Log;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by darrankelinske on 5/2/16.
 */
public class EventsPresenter implements EventsContract.UserActionsListener {

    @Inject
    DatabaseReference firebase;
    @Inject
    EventsRepository repository;

    EventsContract.View view;

    public EventsPresenter(EventsContract.View view, Activity activity) {
        this.view = view;
        ((AustinFeedsMeApplication) activity.getApplication()).component().inject(this);
    }

    @Override
    public void loadEvents() {
        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                view.showEvents(events);
            }

            @Override
            public void onError(String error) {
                Log.e("OOPS", "We have an errorrrrr");

            }
        });

    }

    @Override
    public void openEventDetails(Event clickedEvent) {

    }
}
