package com.austindroids.austinfeedsme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class Results {

    @SerializedName("results")
    @Expose
    private List<Event> events;

    /**
     * @return The events
     */
    public List<Event> getEvents() {
        return events;
    }

}
