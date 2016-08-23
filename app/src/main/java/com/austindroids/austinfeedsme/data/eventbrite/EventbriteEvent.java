package com.austindroids.austinfeedsme.data.eventbrite;

import com.austindroids.austinfeedsme.data.Group;
import com.austindroids.austinfeedsme.data.Venue;
import com.google.gson.annotations.SerializedName;

/**
 * Created by daz on 8/19/16.
 */

public class EventbriteEvent {
    @SerializedName("name")
    private Name name;
    @SerializedName("description")
    private Description description;
    @SerializedName("id")
    private String id;
    @SerializedName("start")
    private Start start;
    @SerializedName("end")
    private End end;
    @SerializedName("url")
    private String url;
    @SerializedName("category_id")
    private String category_id;
    @SerializedName("venue_id")
    private String venue_id;
    @SerializedName("organizer_id")
    private String organizer_id;
    @SerializedName("logo")
    Logo logo;
    private Venue venue;
    private Group group;
    private Organizer organizer;

    public EventbriteEvent() {

    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public void setEnd(End end) {
        this.end = end;
    }

    public Start getStart() {
        return start;
    }

    public End getEnd() {
        return end;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }


    class BaseEventbriteField {

        String text;
        String html;

        public String getText() {
            return text;
        }
    }

    public class Name extends BaseEventbriteField {
    }

    public class Description extends BaseEventbriteField {
    }

    public class Start {

        String local;
        String utc;
        public String getLocal() {
            return local;
        }
        public String getUtc() {
            return utc;
        }
    }

    class End extends Start {
    }

    class Logo {
        String url;
    }

    public class Organizer {
        public String getName() {
            return name;
        }

        String name;
    }


    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getOrganizer_id() {
        return organizer_id;
    }

    public Venue getVenue() {
        return venue;
    }

    public Group getGroup() {
        return group;
    }
}
