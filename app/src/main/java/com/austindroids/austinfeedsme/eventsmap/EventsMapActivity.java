package com.austindroids.austinfeedsme.eventsmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.common.BaseActivity;
import com.austindroids.austinfeedsme.common.EventsContract;
import com.austindroids.austinfeedsme.common.EventsPresenter;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.events.EventsFilterType;
import com.austindroids.austinfeedsme.utility.DateUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by daz on 8/5/16.
 */
public class EventsMapActivity extends BaseActivity implements
        EventsContract.View,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener {

    public static final String TAG = EventsMapActivity.class.getSimpleName();

    GoogleMap map;
    SupportMapFragment mapFragment;
    private ViewPager viewPager;
    CardPagerAdapter adapter;

    @Inject
    EventsRepository repository;

    @Inject
    EventsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        Toolbar mapToolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(mapToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Event selectedEvent = adapter.getEventAtPosition(position);
                LatLng selectedLocation = new LatLng(Double.parseDouble(selectedEvent.getVenue().getLat()),
                        Double.parseDouble(selectedEvent.getVenue().getLon()));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng austin = new LatLng(30.27415, -97.73996);
        this.map = map;
        this.map.setMyLocationEnabled(true);
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(austin, 13));
        this.map.setOnInfoWindowLongClickListener(this);

        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //reference marker's event's position in arraylist (marker.getTag)
                //viewpager.setcurrentitem(position)
                int markerPosition = (int) marker.getTag();
                //Toast.makeText(EventsMapActivity.this, marker.getTag().toString(), Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(markerPosition);
                return true;
            }
        });
        presenter.loadEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventsmap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(EventsMapActivity.this, findViewById(R.id.map_filter));
        popup.getMenuInflater().inflate(R.menu.filter_events, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.todays_events:
                        presenter.setFiltering(EventsFilterType.TODAYS_EVENTS);
                        break;
                    case R.id.this_weeks_events:
                        presenter.setFiltering(EventsFilterType.THIS_WEEKS_EVENTS);
                        break;
                    default:
                        presenter.setFiltering(EventsFilterType.ALL_EVENTS);
                        break;
                }
                presenter.loadEvents();
                return true;
            }
        });

        popup.show();
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {

        String rsvpLink = (String) marker.getTag();

        if (rsvpLink != null)
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(rsvpLink));
            startActivity(i);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showFilteringPopUpMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showEvents(List<Event> events) {
        map.clear();

        adapter = new CardPagerAdapter(events);
        viewPager.setAdapter(adapter);

        for (Event event : events) {
            if (event.getVenue() == null || event.getFoodType() == null) {
                Log.v(TAG, "The venue or food type for the following event was null: "
                        +event.getName());
                continue;
            }
            LatLng eventLocation = new LatLng(
                    Double.valueOf(event.getVenue().getLat()),
                    Double.valueOf(event.getVenue().getLon()));
            if (event.getFoodType().equals("beer")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer_emoji))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else if (event.getFoodType().equals("pizza")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pizza_emoji_smaller))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else if (event.getFoodType().equals("tacos")) {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taco_emoji))
                        .snippet(event.getGroup().getName() + "\n" + event.getName()))
                        .setTag(events.indexOf(event));
            } else {
                map.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.apple_emoji)))
                        .setTag(events.indexOf(event));
            }
        }
    }

    @Override
    public void setPizzaCount(int count) {

    }

    @Override
    public void setTacoCount(int count) {

    }

    @Override
    public void setBeerCount(int count) {

    }

    @Override
    public void setTotalCount(int count) {

    }

    @Override
    public void showNoEventsView() {
        map.clear();
        viewPager.setAdapter(null);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }
}
