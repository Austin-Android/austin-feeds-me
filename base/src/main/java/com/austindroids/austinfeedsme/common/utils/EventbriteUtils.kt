package com.austindroids.austinfeedsme.common.utils

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.Group
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEvent

/**
 * Created by daz on 8/19/16.
 */

object EventbriteUtils {

    fun transformEventBrite(event: EventbriteEvent): Event {

        val returnEvent = Event(event.id, event.name.text,
                event.description.text, DateUtils.getUnixTimeFromISO8601(event.start.utc),
                event.url, event.food_type)

        returnEvent.venue = event.venue
        returnEvent.group = Group(event.organizer.name)

        return returnEvent
    }

}
