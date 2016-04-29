package com.example.utfeedsme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.utfeedsme.data.Event;
import com.example.utfeedsme.data.EventsDataSource;
import com.example.utfeedsme.data.EventsRepository;
import com.example.utfeedsme.data.FirebaseEventsDataSource;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

import javax.inject.Inject;

public class AllEvents extends Activity {
    private static final String LOG_TAG = "AllEvents";

    @Inject EventsRepository repository;

    private ParseQueryAdapter<ParseObject> mainAdapter;
    private CustomAdapter urgentTodosAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AustinFeedsMeApplication) getApplication()).component().inject(this);
        setContentView(R.layout.all_events);

        repository.getEvents(new EventsDataSource.LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                for(Event event : events) {
                    Log.i(LOG_TAG, "We got the following event: " + event.getTitle());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(LOG_TAG, error);

            }
        });

        // Initialize main ParseQueryAdapter
        mainAdapter = new ParseQueryAdapter<ParseObject>(this, "FoodEvent");
        mainAdapter.setTextKey("event");
        mainAdapter.setImageKey("Image");

        // Initialize the subclass of ParseQueryAdapter
        urgentTodosAdapter = new CustomAdapter(this);

        // Initialize ListView and set initial view to mainAdapter
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mainAdapter);
        mainAdapter.loadObjects();

        // Initialize toggle button
        Button toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listView.getAdapter() == mainAdapter) {
                    listView.setAdapter(urgentTodosAdapter);
                    urgentTodosAdapter.loadObjects();
                } else {
                    listView.setAdapter(mainAdapter);
                    mainAdapter.loadObjects();
                }
            }

        });
    }

}