package com.austindroids.austinfeedsme.common.utils

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.Group
import com.austindroids.austinfeedsme.data.GroupPhoto
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEvent

/**
 * Created by daz on 8/19/16.
 */

object EventbriteUtils {

    fun transformEventBrite(eventbriteEvent: EventbriteEvent): Event {

        val returnEvent = Event(eventbriteEvent.id, eventbriteEvent.name.text,
                eventbriteEvent.description.text, DateUtils.getUnixTimeFromISO8601(eventbriteEvent.start.utc),
                eventbriteEvent.url, eventbriteEvent.food_type)

        returnEvent.venue = eventbriteEvent.venue
        returnEvent.group = Group(eventbriteEvent.organizer.name)
        eventbriteEvent.logo?.originalLogo?.logoUrl?.run {
            returnEvent.group.groupPhoto = GroupPhoto(this)
        }

        return returnEvent
    }

}
