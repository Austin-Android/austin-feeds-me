package com.austindroids.austinfeedsme.events

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.common.base.BaseActivity
import com.austindroids.austinfeedsme.common.events.EventsContract
import com.austindroids.austinfeedsme.common.events.EventsPresenter
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.eventsfilter.EventFilterActivity
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.inject.Inject

class EventsActivity : BaseActivity(), EventsContract.View {

    @Inject lateinit var eventsPresenter: EventsPresenter

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var noEventsLinearLayout: View
    private lateinit var progressOverlay: FrameLayout
    private lateinit var navigationView: NavigationView

    private var drawerToggle: ActionBarDrawerToggle? = null
    private var searchViewForMenu: SearchView? = null
    private var eventListAdapter: EventsAdapter? = null
    private var eventItemListener: EventsAdapter.EventItemListener = getEventItemListener()
    private lateinit var navigationEventsFilter: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        drawerLayout = findViewById(R.id.drawer_layout_events)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_events)
        eventsRecyclerView = findViewById(R.id.recycler_view_events)
        noEventsLinearLayout = findViewById(R.id.linear_layout_no_events)
        progressOverlay = findViewById(R.id.progress_overlay)
        navigationView = findViewById(R.id.navigation_view_events)

        setupActionBar()
        setupMenu()
        setupSwipeToRefresh()
        setupEventsList()
        setupNavigationDrawer(navigationView)

        eventsPresenter.loadEvents()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_events, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchViewForMenu = menu.findItem(R.id.menu_search).actionView as SearchView
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)
        searchView.maxWidth = Integer.MAX_VALUE

        val searchMenu = menu.findItem(R.id.menu_search)

        MenuItemCompat.setOnActionExpandListener(searchMenu,
                object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        eventsPresenter!!.loadEvents()
                        return true  // Return true to collapse action view
                    }

                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        // Do something when expanded
                        return true  // Return true to expand action view
                    }
                })

        // Get the search close button image view
        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView

        // Set on click listener
        closeButton.setOnClickListener {
            //Find EditText view
            val et = findViewById<View>(R.id.search_src_text) as EditText

            //Clear the text from EditText view
            et.setText("")

            //Clear query
            searchView.setQuery("", false)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            //Collapse the search widget
            searchMenu.collapseActionView()
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.login_menu_item).isVisible = FirebaseAuth.getInstance().currentUser == null
        menu.findItem(R.id.logout_menu_item).isVisible = FirebaseAuth.getInstance().currentUser != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }

            R.id.map_filter -> showFilteringPopUpMenu()

            R.id.login_menu_item -> {

                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN)
                return true
            }

            R.id.logout_menu_item -> {
                AuthUI.getInstance(FirebaseApp.getInstance()!!)
                        .signOut(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("EventsActivity", "This is the current email: " + FirebaseAuth.getInstance().currentUser!!.email!!)
                Log.d("EventsActivity", "This is the current uid: " + FirebaseAuth.getInstance().currentUser!!.uid)
            }
        }
    }

    override fun setPizzaCount(count: Int) {
        // get menu from navigationView
        val menu = navigationView!!.menu
        // find MenuItem you want to change
        val navigationPizzaEvents = menu.findItem(R.id.events_pizza)
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.title = "Pizza: $count"
    }

    override fun setTacoCount(count: Int) {
        // get menu from navigationView
        val menu = navigationView!!.menu
        // find MenuItem you want to change
        val navigationPizzaEvents = menu.findItem(R.id.events_tacos)
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.title = "Tacos: $count"

    }

    override fun setBeerCount(count: Int) {// get menu from navigationView
        val menu = navigationView!!.menu
        // find MenuItem you want to change
        val navigationPizzaEvents = menu.findItem(R.id.events_beer)
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.title = "Beer: $count"

    }

    override fun setTotalCount(count: Int) {// get menu from navigationView
        val menu = navigationView!!.menu
        // find MenuItem you want to change
        val navigationPizzaEvents = menu.findItem(R.id.events_list)
        //Check if user logged in, change sign in/out button to correct text
        navigationPizzaEvents.title = "Events List: $count"

    }

    override fun showNoEventsView() {
        eventsRecyclerView!!.visibility = View.GONE
        noEventsLinearLayout!!.visibility = View.VISIBLE
    }


    override fun showProgress() {
        progressOverlay.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressOverlay.visibility = View.GONE
    }

    private fun getEventItemListener(): EventsAdapter.EventItemListener {
        return object : EventsAdapter.EventItemListener {
            override fun onEventClick(clickedEvent: Event) {}
        }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            eventsPresenter!!.searchEvents(query)
        }
    }

    private fun setupNavigationDrawer(navigationView: NavigationView) {

        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                drawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */) {
            override fun onDrawerClosed(drawerView: View) {
                supportActionBar?.title = title
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportActionBar?.title = title
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }

        drawerToggle!!.isDrawerIndicatorEnabled = true

        drawerLayout.post {
            // To display hamburger icon in toolbar
            drawerToggle!!.syncState()
        }

        drawerLayout.addDrawerListener(drawerToggle!!)
    }

    override fun showFilteringPopUpMenu() {
        val popup = PopupMenu(this@EventsActivity, findViewById(R.id.map_filter))
        popup.menuInflater.inflate(R.menu.filter_events, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.todays_events -> eventsPresenter!!.setFiltering(EventsFilterType.TODAYS_EVENTS)
                R.id.this_weeks_events -> eventsPresenter!!.setFiltering(EventsFilterType.THIS_WEEKS_EVENTS)
                else -> eventsPresenter!!.setFiltering(EventsFilterType.ALL_EVENTS)
            }
            eventsPresenter!!.loadEvents()
            true
        }

        popup.show()
    }

    private fun selectDrawerItem(menuItem: MenuItem) {

        when (menuItem.itemId) {
            R.id.events_list -> Handler().postDelayed({ searchViewForMenu!!.setQuery("reset", true) }, 300)
            R.id.events_map -> Handler().postDelayed({ startActivity(Intent(this@EventsActivity, EventsMapActivity::class.java)) }, 300)
            R.id.events_filter -> Handler().postDelayed({ startActivity(Intent(this@EventsActivity, EventFilterActivity::class.java)) }, 300)
            R.id.events_pizza -> Handler().postDelayed({ searchViewForMenu!!.setQuery("pizza", true) }, 300)
            R.id.events_tacos -> Handler().postDelayed({ searchViewForMenu!!.setQuery("taco", true) }, 300)
            R.id.events_beer -> Handler().postDelayed({ searchViewForMenu!!.setQuery("beer", true) }, 300)
            R.id.privacy_policy -> {
                val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://whereisdarran.com/privacy_policy.html"))
                startActivity(privacyIntent)
            }
            else -> startActivity(Intent(this@EventsActivity, EventsActivity::class.java))
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMenu() {
        // get menu from navigationView
        val menu = navigationView.menu

        // find MenuItem you want to change
        navigationEventsFilter = menu.findItem(R.id.events_filter)

        //Check if user logged in, change sign in/out button to correct text

        navigationEventsFilter.isVisible = FirebaseAuth.getInstance().currentUser != null
    }

    override fun showEvents(events: List<Event>) {
        eventListAdapter!!.replaceData(events)
        eventsRecyclerView!!.visibility = View.VISIBLE
        noEventsLinearLayout!!.visibility = View.GONE
    }

    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener { refreshEvents() }
    }

    private fun refreshEvents() {
        Handler().postDelayed({
            setupEventsList()
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    private fun setupEventsList() {
        eventListAdapter = EventsAdapter(this@EventsActivity, ArrayList(0),
                eventItemListener)

        eventsRecyclerView.adapter = eventListAdapter
        eventsRecyclerView.setHasFixedSize(true)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    companion object {

        const val RC_SIGN_IN = 7
    }
}
