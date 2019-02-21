package com.austindroids.austinfeedsme.common.events

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.events.EventsFilterType

/**
 * Created by darrankelinske on 5/2/16.
 */
interface EventsContract {

    interface View {
        fun showEvents(events: List<Event>)
        fun setPizzaCount(count: Int?)
        fun setTacoCount(count: Int?)
        fun setBeerCount(count: Int?)
        fun setTotalCount(count: Int)
        fun showFilteringPopUpMenu()
        fun showNoEventsView()
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun loadEvents()
        fun refreshEvents()
        fun searchEvents(searchTerm: String)
        fun setFiltering(requestType: EventsFilterType)
    }
}
