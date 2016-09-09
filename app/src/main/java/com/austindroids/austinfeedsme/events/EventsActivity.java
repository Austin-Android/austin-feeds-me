package com.austindroids.austinfeedsme.events;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.eventsfilter.EventFilterActivity;
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity;
import com.austindroids.austinfeedsme.utility.DateUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class EventsActivity extends AppCompatActivity
        implements EventsContract.View {

    private final static String TAG = "EventsActivity";

    public static final int RC_SIGN_IN = 7;

    // Navigation Menu member variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    RecyclerView mRecyclerView;
    private View mNoEventsView;

    private SearchView searchViewForMenu;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private EventsContract.Presenter eventsPresenter;

    private EventsAdapter mListAdapter;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAnalytics firebaseAnalytics;

    @Inject
    EventsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ((AustinFeedsMeApplication) this.getApplication()).component().inject(this);

        eventsPresenter = new EventsPresenter(repository, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView)  findViewById(R.id.navigation_view);
        mNoEventsView = findViewById(R.id.noEventsView);
        setupDrawerContent(mNavigationView);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */)
        {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                // To display hamburger icon in toolbar
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mListAdapter = new EventsAdapter(EventsActivity.this, new ArrayList<Event>(0),
                mEventItemListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.event_list_recycler_view);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventsPresenter.loadEvents();

        //Todo: Iterate through list once creating a Hashmap of counts
        eventsPresenter.loadYummyCounts();

        // get menu from navigationView
        Menu menu = mNavigationView.getMenu();

        // find MenuItem you want to change
        final MenuItem navigationEventsFilter = menu.findItem(R.id.events_filter);

        //Check if user logged in, change sign in/out button to correct text

        navigationEventsFilter.setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                navigationEventsFilter.setVisible(
                        FirebaseAuth.getInstance().getCurrentUser() != null);
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    /**
     * Listener for clicks on events in the RecyclerView.
     */
    EventItemListener mEventItemListener = new EventItemListener() {
        @Override
        public void onEventClick(Event clickedEvent) {
            eventsPresenter.openEventDetails(clickedEvent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchViewForMenu = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        // Fix to have searchview expand to fill entire ActionBar on tablets
        searchView.setMaxWidth(Integer.MAX_VALUE);

        final MenuItem searchMenu = menu.findItem(R.id.menu_search);

        MenuItemCompat.setOnActionExpandListener(searchMenu,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        eventsPresenter.loadEvents();
                        return true;  // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true;  // Return true to expand action view
                    }
                });

        // Get the search close button image view
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Find EditText view
                EditText et = (EditText) findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                //Collapse the search widget
                searchMenu.collapseActionView();
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.login_menu_item)
                .setVisible(FirebaseAuth.getInstance().getCurrentUser() == null);
        menu.findItem(R.id.logout_menu_item)
                .setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.map_filter:
                showFilteringPopUpMenu();
                break;

            case R.id.login_menu_item:

                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(AuthUI.EMAIL_PROVIDER).build(),
                        RC_SIGN_IN);
                return true;

            case R.id.logout_menu_item:

                AuthUI.getInstance(FirebaseApp.getInstance())
                        .signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
//                        AlphaAnimation animation1 = new AlphaAnimation(1, 0);
//                        animation1.setDuration(1000);
//                        animation1.setStartOffset(1000);
//                        animation1.setFillAfter(true);
//                        mAddEventFab.startAnimation(animation1);
//                        mAddEventFab.setVisibility(View.GONE);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
//            mAddEventFab.setVisibility(View.VISIBLE);
//        } else {
//            mAddEventFab.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onPause() {
      super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            eventsPresenter.searchEvents(query);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(EventsActivity.this, findViewById(R.id.map_filter));
        popup.getMenuInflater().inflate(R.menu.filter_events, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.todays_events:
                        eventsPresenter.setFiltering(EventsFilterType.TODAYS_EVENTS);
                        break;
                    case R.id.this_weeks_events:
                        eventsPresenter.setFiltering(EventsFilterType.THIS_WEEKS_EVENTS);
                        break;
                    default:
                        eventsPresenter.setFiltering(EventsFilterType.ALL_EVENTS);
                        break;
                }
                eventsPresenter.loadEvents();
                return true;
            }
        });

        popup.show();
    }


    public void selectDrawerItem(MenuItem menuItem) {

//        Intent searchIntent = new Intent();
//        searchIntent.setAction(Intent.ACTION_SEARCH);

        switch (menuItem.getItemId()) {
            case R.id.events_list:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                searchViewForMenu.setQuery("reset", true);
                    }
                }, 300);
                break;
            case R.id.events_map:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(EventsActivity.this, EventsMapActivity.class));
                    }
                }, 300);
                //startActivity(new Intent(EventsActivity.this, EventsMapActivity.class));
                break;
            case R.id.events_filter:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                startActivity(new Intent(EventsActivity.this, EventFilterActivity.class));
                    }
                }, 300);
                break;
            case R.id.events_pizza:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                searchViewForMenu.setQuery("pizza", true);
                    }
                }, 300);
                break;
            case R.id.events_tacos:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                searchViewForMenu.setQuery("taco", true);
                    }
                }, 300);

                // Log an event when tacos are selected!
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "tacos");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;
            case R.id.events_beer:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                searchViewForMenu.setQuery("beer", true);
                    }
                }, 300);
                break;
            default:
                startActivity(new Intent(EventsActivity.this, EventsActivity.class));
                break;

        }
    }

    @Override
    public void showEvents(List<Event> events) {
        mListAdapter.replaceData(events);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoEventsView.setVisibility(View.GONE);
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

        private Context context;
        private List<Event> mEvents;
        private EventItemListener mItemListener;

        public EventsAdapter(Context context, List<Event> Events, EventItemListener itemListener) {
            this.context = context;
            setList(Events);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View EventView = inflater.inflate(R.layout.item_event, parent, false);

            return new ViewHolder(EventView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final Event event = mEvents.get(position);

            viewHolder.eventDate.setText(DateUtils.getLocalDateFromTimestamp(event.getTime()));
            viewHolder.title.setText(event.getName());

            Spanned result;
            Spanned rsvpLink;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(event.getDescription(),Html.FROM_HTML_MODE_LEGACY);
                rsvpLink = Html.fromHtml(
                        "<html><a href=\""+event.getEvent_url()+"\">RSVP Here!</a></html>",
                        Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(event.getDescription());
                rsvpLink = Html.fromHtml(
                        "<html><a href=\""+event.getEvent_url()+"\">RSVP Here!</a></html>");
            }
            viewHolder.description.setText(result);


            viewHolder.eventUrl.setMovementMethod(LinkMovementMethod.getInstance());
//            viewHolder.eventUrl.setText(rsvpLink);
            viewHolder.eventUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(event.getEvent_url()));
                    context.startActivity(webIntent);
                }
            });
        }

        public void replaceData(List<Event> Events) {
            setList(Events);
            notifyDataSetChanged();
        }

        private void setList(List<Event> Events) {
            mEvents = Events;
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public Event getItem(int position) {
            return mEvents.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView eventDate;
            public TextView title;
            public TextView description;
            public Button eventUrl;
            private EventItemListener mItemListener;

            public ViewHolder(View itemView, EventItemListener listener) {
                super(itemView);
                mItemListener = listener;
                eventDate = (TextView) itemView.findViewById(R.id.event_detail_time);
                title = (TextView) itemView.findViewById(R.id.event_detail_title);
                description = (TextView) itemView.findViewById(R.id.event_detail_description);
                eventUrl = (Button) itemView.findViewById(R.id.event_link);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Event Event = getItem(position);
                mItemListener.onEventClick(Event);

            }
        }
    }

    public interface EventItemListener {

        void onEventClick(Event clickedEvent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d("EventsActivity", "This is the current email: " +
                        FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Log.d("EventsActivity", "This is the current uid: " +
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
//                        AlphaAnimation animation1 = new AlphaAnimation(0, 1);
//                        animation1.setDuration(1000);
//                        animation1.setStartOffset(1000);
//                        animation1.setFillAfter(true);
//                        mAddEventFab.startAnimation(animation1);
//                        mAddEventFab.setVisibility(View.VISIBLE);
                        DatabaseReference userReference =
                                FirebaseDatabase.getInstance().getReference("user");
                        userReference.child(
                                FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
        }

    }

    @Override
    public void setPizzaCount(int count) {
        // get menu from navigationView
        Menu menu = mNavigationView.getMenu();
        // find MenuItem you want to change
        final MenuItem navigationPizzaEvents = menu.findItem(R.id.events_pizza);
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.setTitle("Pizza: " +count);
    }

    @Override
    public void setTacoCount(int count) {
        // get menu from navigationView
        Menu menu = mNavigationView.getMenu();
        // find MenuItem you want to change
        final MenuItem navigationPizzaEvents = menu.findItem(R.id.events_tacos);
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.setTitle("Tacos: " +count);

    }

    @Override
    public void setBeerCount(int count) {// get menu from navigationView
        Menu menu = mNavigationView.getMenu();
        // find MenuItem you want to change
        final MenuItem navigationPizzaEvents = menu.findItem(R.id.events_beer);
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.setTitle("Beer: " +count);

    }

    @Override
    public void setTotalCount(int count) {// get menu from navigationView
        Menu menu = mNavigationView.getMenu();
        // find MenuItem you want to change
        final MenuItem navigationPizzaEvents = menu.findItem(R.id.events_list);
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.setTitle("Events List: " +count);

    }

    @Override
    public void showNoEventsView() {
        mRecyclerView.setVisibility(View.GONE);
        mNoEventsView.setVisibility(View.VISIBLE);
    }
}
