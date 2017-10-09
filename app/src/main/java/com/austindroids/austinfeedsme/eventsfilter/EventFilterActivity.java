package com.austindroids.austinfeedsme.eventsfilter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.common.BaseActivity;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import hugo.weaving.DebugLog;

import static com.austindroids.austinfeedsme.utility.NetworkUtils.isNetworkAvailable;


/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends BaseActivity implements EventFilterContract.View {

    EventFilterPresenter eventFilterPresenter;

    final EventFilterAdapter eventFilterAdapter = new EventFilterAdapter(new ArrayList<Event>());

    @Inject @Named("eventbrite") EventsDataSource eventbriteDataSource;
    @Inject @Named("meetup") EventsDataSource meetupDataSource;

    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);

        RecyclerView eventsRecyclerView = findViewById(R.id.event_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventFilterAdapter);

        EventsRepository eventbriteRepository = new EventsRepository(eventbriteDataSource);
        EventsRepository meetupRepository = new EventsRepository(meetupDataSource);

        eventFilterPresenter =
                new EventFilterPresenter(eventbriteRepository, meetupRepository, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable(this)) {
            showSnackbar(getString(R.string.network_unavailable));
        }
        eventFilterAdapter.replaceData(new ArrayList<Event>());
        eventFilterPresenter.loadEvents();
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
