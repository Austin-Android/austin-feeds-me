package com.austindroids.austinfeedsme.eventsfilter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEventsDateSource;
import com.austindroids.austinfeedsme.data.meetup.MeetupDataSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends AppCompatActivity implements EventFilterContract.View {

    private static final String TAG ="EventFilterActivity";

    private RecyclerView eventsRecyclerView;

    EventFilterPresenter eventFilterPresenter;

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

        EventsRepository eventbriteRepository = new EventsRepository(new EventbriteEventsDateSource());
        EventsRepository meetupRepository = new EventsRepository(new MeetupDataSource());
        eventFilterPresenter =
                new EventFilterPresenter(eventbriteRepository, meetupRepository, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
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
