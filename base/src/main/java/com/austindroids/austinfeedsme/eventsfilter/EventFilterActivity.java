package com.austindroids.austinfeedsme.eventsfilter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.common.base.BaseActivity;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;



/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends BaseActivity implements EventFilterContract.View {

    @Inject @Named("eventbrite") EventsDataSource eventbriteDataSource;
    @Inject @Named("meetup") EventsDataSource meetupDataSource;
    @Inject EventsRepository eventsRepository;

    private EventFilterAdapter eventFilterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);

        RecyclerView eventsRecyclerView = findViewById(R.id.event_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventFilterAdapter = new EventFilterAdapter(eventsRepository);
        eventsRecyclerView.setAdapter(eventFilterAdapter);

        // Move these dependencies into the dependency graph
        EventsRepository eventbriteRepository = new EventsRepository(eventbriteDataSource);
        EventsRepository meetupRepository = new EventsRepository(meetupDataSource);

        EventFilterPresenter eventFilterPresenter = new EventFilterPresenter(eventbriteRepository, meetupRepository, this);
        eventFilterPresenter.loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showEvents(List<Event> events) {
        eventFilterAdapter.addEvents(events);
    }
}
