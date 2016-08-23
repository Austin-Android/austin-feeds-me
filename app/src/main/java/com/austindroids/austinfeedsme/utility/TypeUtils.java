package com.austindroids.austinfeedsme.utility;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.Group;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEvent;

/**
 * Created by daz on 8/19/16.
 */

public class TypeUtils {

    public static Event transformEventBrite(EventbriteEvent event) {

        Event returnEvent = new Event(event.getId(), event.getName().getText(),
                event.getDescription().getText(), DateUtils.getUnixTimeFromISO8601(event.getStart().getUtc()),
                event.getUrl());

        returnEvent.setVenue(event.getVenue());
        returnEvent.setGroup(new Group(event.getOrganizer().getName()));

        return returnEvent;
    }

}
