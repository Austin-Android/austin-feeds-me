package com.austindroids.austinfeedsme.eventsfilter

import com.austindroids.austinfeedsme.data.Event

/**
 * Created by darrankelinske on 8/4/16.
 */
interface EventFilterContract {

    interface View {
        fun showEvents(events: List<Event>)
    }

    interface Presenter {
        fun loadEvents()
        fun dispose();
    }
}