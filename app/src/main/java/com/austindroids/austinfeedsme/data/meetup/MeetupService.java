package com.austindroids.austinfeedsme.data.meetup;

import com.austindroids.austinfeedsme.Constants;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by darrankelinske on 8/4/16.
 */
public interface MeetupService {

    @GET("/{group_urlname}/events/")
    Call<List<Event>> getEvents(@Path("group_urlname") String group_urlname);

    @GET("/{group_urlname}/events/{event_id}")
    Call<Event> getEvent(@Path("group_urlname") String group_urlname,
                         @Path("event_id") String event_id);

    @GET("/2/open_events?key="+ Constants.MEETUP_KEY+"" +
            "&sign=true" +
            "&photo-host=public" +
            "&zip=78701" +
            "&text=pizza" +
            "&text=provided" +
            "&text=beer" +
            "&text=breakfast" +
            "&text=lunch" +
            "&text=dinner" +
            "&text=taco" +
            "&page=300" +
            "&radius=40")
    Observable<Results> getOpenEvents();
}
