package com.austindroids.austinfeedsme.eventsmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.utility.DateUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by daz on 8/5/16.
 */
public class EventsMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener {

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng austin = new LatLng(30.27415, -97.73996);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(austin, 13));
        map.setOnInfoWindowLongClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("events");
        myRef.keepSynced(true);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);

                    if (event.isFood()
                            && event.getVenue() != null
                            && event.getTime() > new Date().getTime()) {

                        LatLng eventLocation = new LatLng(
                                Double.valueOf(event.getVenue().getLat()),
                                Double.valueOf(event.getVenue().getLon()));
                        map.addMarker(new MarkerOptions()
                                .position(eventLocation)
                                .title(DateUtils.getLocalDateFromTimestamp(event.getTime()))
                                .snippet(event.getGroup().getName() + "\n" +event.getName()))
                                .setTag(event.getEvent_url());

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}