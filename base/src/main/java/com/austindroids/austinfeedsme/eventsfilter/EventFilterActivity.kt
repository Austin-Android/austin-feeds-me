package com.austindroids.austinfeedsme.eventsfilter

import android.os.Bundle
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.common.base.BaseActivity
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.FilterableEventsRepository
import kotlinx.android.synthetic.main.activity_event_filter.*
import javax.inject.Inject

/**
 * Created by darrankelinske on 8/4/16.
 */
class EventFilterActivity : BaseActivity(), EventFilterContract.View {

    @Inject
    internal lateinit var eventFilterPresenter: EventFilterPresenter
    @Inject
    internal lateinit var eventsRepository: FilterableEventsRepository

    private var eventFilterAdapter: EventFilterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_filter)

        eventFilterAdapter = EventFilterAdapter(eventsRepository)
        event_recycler_view.adapter = eventFilterAdapter

        eventFilterPresenter.loadEvents()
    }

    override fun showEvents(events: List<Event>) {
        eventFilterAdapter?.addEvents(events)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventFilterPresenter.dispose()
    }
}
