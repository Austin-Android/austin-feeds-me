package com.example.utfeedsme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class AllEvents extends Activity {

    private ParseQueryAdapter<ParseObject> mainAdapter;
    private CustomAdapter urgentTodosAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

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