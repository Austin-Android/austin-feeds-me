package com.austindroids.austinfeedsme.data.meetup;

import com.austindroids.austinfeedsme.Constants;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by darrankelinske on 8/4/16.
 */
public interface MeetupService {

    @GET("/{group_urlname}/events/")
    Call<List<Event>> getEvents(@Path("group_urlname") String group_urlname);

    @GET("/{group_urlname}/events/{event_id}")
    Call<Event> getEvent(@Path("group_urlname") String group_urlname,
                         @Path("event_id") String event_id);

    @GET("/2/open_events?key="+ Constants.MEETUP_KEY+"&sign=true&photo-host=public&zip=78731&text=pizza&text=provided&page=300&radius=30")
    Call<Results> getEventsWithPizza();
}
