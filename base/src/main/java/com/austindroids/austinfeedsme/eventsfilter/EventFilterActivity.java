package com.austindroids.austinfeedsme.eventsfilter;

import android.os.Bundle;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.common.base.BaseActivity;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends BaseActivity implements EventFilterContract.View {

    @Inject @Named("eventbrite") EventsDataSource eventbriteDataSource;
    @Inject @Named("meetup") EventsDataSource meetupDataSource;
    @Inject FilterableEventsRepository eventsRepository;

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

        EventFilterPresenter eventFilterPresenter =
                new EventFilterPresenter(eventsRepository, eventbriteRepository, meetupRepository, this);
        eventFilterPresenter.loadEvents();
    }

    @Override
    public void showEvents(List<Event> events) {
        eventFilterAdapter.addEvents(events);
    }
}
