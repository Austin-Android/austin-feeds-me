package com.austindroids.austinfeedsme.data.eventbrite;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by daz on 8/19/16.
 */

public class EventbriteEvents {
    @SerializedName("pagination")
    Pagination pagination;
    @SerializedName("events")
    List<EventbriteEvent> events;

    public EventbriteEvents() {
    }

    public List<EventbriteEvent> getEvents() {
        return events;
    }

    public void setEvents(List<EventbriteEvent> events) {
        this.events = events;
    }


    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    class Pagination {
        @SerializedName("object_count")
        String object_count;
        @SerializedName("page_number")
        String page_number;
        @SerializedName("page_size")
        String page_size;
        @SerializedName("page_count")
        String page_count;
    }
}