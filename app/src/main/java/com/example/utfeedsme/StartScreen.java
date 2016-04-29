package com.example.utfeedsme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.utfeedsme.addeditevent.AddEditEventActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;
import com.parse.Parse;
import com.parse.ParseObject;

public class StartScreen extends FirebaseLoginBaseActivity
        implements NavigationMenuAdapter.OnItemClickListener {

    private Firebase mRef;

    // Navigation Menu member variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationItems;
	
	private final static String TAG = "StartScreen";
	
	//protected RecordsDataSource dataSource;
		
	ImageButton happening_now_btn, near_you_btn, all_events_btn, add_event_btn, add_event_btn2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        mRef = new Firebase("https://austin-feeds-me.firebaseio.com/");
        
       // final StartScreen thisActivity = this;
        Parse.initialize(this, "vdhZN2rmjBYhLJFlFK8NRFW0wKZHQ3CDNMEkwAWy", BuildConfig.PARSE_KEY);
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        //dataSource = new RecordsDataSource(this);
        //dataSource.open();

        mTitle = mDrawerTitle = getTitle();
        
        happening_now_btn = (ImageButton) findViewById(R.id.happening_now);
        near_you_btn = (ImageButton) findViewById(R.id.near_you);
        all_events_btn = (ImageButton) findViewById(R.id.all_events);
        add_event_btn = (ImageButton) findViewById(R.id.add_event);
        add_event_btn2 = (ImageButton) findViewById(R.id.add_event2);

        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationItems = getResources().getStringArray(R.array.navigation_items_array);

        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new NavigationMenuAdapter(mNavigationItems, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer

        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);




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

        add_event_btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent openAddEvent = new Intent(StartScreen.this, AddEditEventActivity.class);
                Log.v(TAG, "yoooo we pressed the add event 2 button");
                startActivity(openAddEvent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setEnabledAuthProvider(AuthProviderType.TWITTER);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
//        setEnabledAuthProvider(AuthProviderType.PASSWORD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_screen, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.login_menu_item).setVisible(getAuth() == null);
        menu.findItem(R.id.logout_menu_item).setVisible(getAuth() != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_menu_item:
                this.showFirebaseLoginPrompt();
                return true;
            case R.id.logout_menu_item:
                this.logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public Firebase getFirebaseRef() {
        return mRef;
    }

    @Override
    public void onFirebaseLoginProviderError(FirebaseLoginError firebaseError) {
        Log.e(TAG, "Login provider error: " + firebaseError.toString());
        resetFirebaseLoginPrompt();
    }

    @Override
    public void onFirebaseLoginUserError(FirebaseLoginError firebaseError) {
        Log.e(TAG, "Login user error: "+firebaseError.toString());
        resetFirebaseLoginPrompt();
    }


    @Override
    public void onClick(View view, int position) {
        Log.i(TAG, "Menu item with position: " + position + " was clicked.");
        switch(getResources().getStringArray(R.array.navigation_items_array)[position]) {
            case "Event List" :
                startActivity(new Intent(getApplicationContext(), AllEvents.class));
                break;
            case "Event Map" :
                Toast.makeText(StartScreen.this, "Coming soon!", Toast.LENGTH_SHORT).show();
                break;
            case "Add Event" : startActivity(
                    new Intent(getApplicationContext(), AddEditEventActivity.class));
                break;
            default:
                startActivity(new Intent(getApplicationContext(), AllEvents.class));
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);

    }
}
