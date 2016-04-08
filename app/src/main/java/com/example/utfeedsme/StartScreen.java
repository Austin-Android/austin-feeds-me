package com.example.utfeedsme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.parse.Parse;
import com.parse.ParseObject;

public class StartScreen extends Activity {
	
	private final static String TAG = "StartScreen";
	
	//protected RecordsDataSource dataSource;
		
	ImageButton happening_now_btn, near_you_btn, all_events_btn, add_event_btn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        
       // final StartScreen thisActivity = this;
        Parse.initialize(this, "vdhZN2rmjBYhLJFlFK8NRFW0wKZHQ3CDNMEkwAWy", "5J5WzJDG8FR95pmI9eN1HTOCtbcBktoz9B6yRNo4");
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        //dataSource = new RecordsDataSource(this);
        //dataSource.open();
        
        happening_now_btn = (ImageButton) findViewById(R.id.happening_now);
        near_you_btn = (ImageButton) findViewById(R.id.near_you);
        all_events_btn = (ImageButton) findViewById(R.id.all_events);
        add_event_btn = (ImageButton) findViewById(R.id.add_event);
        
        happening_now_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent openHappeningNow = new Intent("com.example.utfeedsme.HAPPENINGNOW");
				startActivity(openHappeningNow);
			}
		});
        
        near_you_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent openNearYou = new Intent("com.example.utfeedsme.NEARYOU");
				startActivity(openNearYou);
			}
		});
        
        all_events_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent openAllEvents = new Intent("com.example.utfeedsme.ALLEVENTS");
				startActivity(openAllEvents);
			}
		});
        
        add_event_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent openAddEvent = new Intent("com.example.utfeedsme.ADDEVENT");
				Log.v(TAG, "yoooo we pressed the add event button");
				startActivity(openAddEvent);
			}
		});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_screen, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Log.d(TAG, "selected settings");
            return true;
        }
        if (id == R.id.sort_pref) {
        	Log.d(TAG, "selected to sort");
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
  */  

    @Override
    protected void onResume() {
      //dataSource.open();
      super.onResume();
    }

    @Override
    protected void onPause() {
      //dataSource.close();
      super.onPause();
    }
}
