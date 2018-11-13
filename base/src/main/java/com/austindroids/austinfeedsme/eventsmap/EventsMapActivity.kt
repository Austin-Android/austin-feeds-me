package com.austindroids.austinfeedsme.eventsmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.common.base.BaseActivity
import com.austindroids.austinfeedsme.common.events.EventsContract
import com.austindroids.austinfeedsme.common.events.EventsPresenter
import com.austindroids.austinfeedsme.common.utils.DateUtils
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.events.EventsFilterType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by daz on 8/5/16.
 */
class EventsMapActivity : BaseActivity(), EventsContract.View, OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener {

    @Inject lateinit var presenter: EventsPresenter

    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var viewPager: ViewPager
    private var cardPagerAdapter: CardPagerAdapter = CardPagerAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_map)

        val mapToolbar = findViewById<View>(R.id.map_toolbar) as Toolbar
        setSupportActionBar(mapToolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewPager = findViewById(R.id.viewPager)
        viewPager?.setPageTransformer(false, ZoomOutSlideTransformer())
        viewPager!!.adapter = cardPagerAdapter
        viewPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                val selectedEvent = cardPagerAdapter.getEventAtPosition(position)
                val selectedLocation = LatLng(java.lang.Double.parseDouble(selectedEvent.venue.lat),
                        java.lang.Double.parseDouble(selectedEvent.venue.lon))
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f))
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


        var events = ArrayList<Event>()

        events.add(Event())
        events.add(Event())
        events.add(Event())
        events.add(Event())
        events.add(Event())
        events.add(Event())

        cardPagerAdapter = CardPagerAdapter(events)
        viewPager!!.adapter = cardPagerAdapter


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setMyLocationEnabled(true)
                } else {
                    setMyLocationEnabled(false)
                }
                return
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val austin = LatLng(30.27415, -97.73996)
        this.map = map
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(austin, 13f))
        this.map.setOnInfoWindowLongClickListener(this)

        this.map.setOnMarkerClickListener { marker ->
            //reference marker's event's position in arraylist (marker.getTag)
            //viewpager.setcurrentitem(position)
            val markerPosition = marker.tag as Int
            //Toast.makeText(EventsMapActivity.this, marker.getTag().toString(), Toast.LENGTH_SHORT).show();
            viewPager!!.currentItem = markerPosition
            true
        }
        presenter!!.loadEvents()

        promptForLocationPermission()
    }

    private fun promptForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        RC_LOCATION_PERMISSION)

            }
        } else {
            setMyLocationEnabled(true)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationEnabled(locationPermissionGranted: Boolean) {
        if (::map.isInitialized) {
            map.isMyLocationEnabled = locationPermissionGranted
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_eventsmap, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun showFilteringPopUpMenu() {
        val popup = PopupMenu(this@EventsMapActivity, findViewById<View>(R.id.map_filter))
        popup.menuInflater.inflate(R.menu.filter_events, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.todays_events -> presenter!!.setFiltering(EventsFilterType.TODAYS_EVENTS)
                R.id.this_weeks_events -> presenter!!.setFiltering(EventsFilterType.THIS_WEEKS_EVENTS)
                else -> presenter!!.setFiltering(EventsFilterType.ALL_EVENTS)
            }
            presenter!!.loadEvents()
            true
        }

        popup.show()
    }


    override fun onInfoWindowLongClick(marker: Marker) {

        val rsvpLink = marker.tag as String?

        if (rsvpLink != null) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(rsvpLink)
            startActivity(i)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showFilteringPopUpMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun showEvents(events: List<Event>) {
        map.clear()

        for (event in events) {
            if (event.venue == null || event.foodType == null) {
                Timber.v("The venue or food type for the following event was null: %s", event.name)
                continue
            }
            val eventLocation = LatLng(
                    java.lang.Double.valueOf(event.venue.lat)!!,
                    java.lang.Double.valueOf(event.venue.lon)!!)
            if (event.foodType == "beer") {
                map.addMarker(MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.time))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer_emoji))
                        .snippet(event.group.name + "\n" + event.name)).tag = events.indexOf(event)
            } else if (event.foodType == "pizza") {
                map.addMarker(MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.time))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pizza_emoji_smaller))
                        .snippet(event.group.name + "\n" + event.name)).tag = events.indexOf(event)
            } else if (event.foodType == "tacos") {
                map.addMarker(MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.time))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taco_emoji))
                        .snippet(event.group.name + "\n" + event.name)).tag = events.indexOf(event)
            } else {
                map.addMarker(MarkerOptions()
                        .position(eventLocation)
                        .title(DateUtils.getLocalDateFromTimestamp(event.time))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.apple_emoji))).tag = events.indexOf(event)
            }
        }
    }

    override fun setPizzaCount(count: Int) {

    }

    override fun setTacoCount(count: Int) {

    }

    override fun setBeerCount(count: Int) {

    }

    override fun setTotalCount(count: Int) {

    }

    override fun showNoEventsView() {
        map?.clear()
        viewPager!!.adapter = null
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    companion object {
        const val RC_LOCATION_PERMISSION = 7
    }

    internal inner class ZoomOutSlideTransformer : BaseTransformer() {


        protected override fun onTransform(view: View, position: Float) {

            val MIN_SCALE = 0.85f
            val MIN_ALPHA = 0.5f

            if (position >= -1 || position <= 1) {
                // Modify the default slide transition to shrink the page as well
                val height = view.height.toFloat()
                val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                val vertMargin = height * (1 - scaleFactor) / 2
                val horzMargin = view.width * (1 - scaleFactor) / 2

                // Center vertically
                view.pivotY = 0.5f * height

                if (position < 0) {
                    view.translationX = horzMargin - vertMargin / 2
                } else {
                    view.translationX = -horzMargin + vertMargin / 2
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                // Fade the page relative to its size.
                view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
        }
    }

}
