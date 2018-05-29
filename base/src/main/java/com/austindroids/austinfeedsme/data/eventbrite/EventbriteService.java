package com.austindroids.austinfeedsme.data.eventbrite;

import com.austindroids.austinfeedsme.common.Constants;
import com.austindroids.austinfeedsme.data.Group;
import com.austindroids.austinfeedsme.data.Venue;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by daz on 8/19/16.
 */

public interface EventbriteService {


    @GET("/v3/events/search/?location.within=40mi&location.latitude=30.27415&location.longitude=" +
            "-97.73996&price=free&expand=organizer,venue&token="+ Constants.EVENTBRITE_TOKEN)
    Observable<EventbriteEvents> getEventsByKeyword(@Query("q") String keyword);

    @GET("/v3/venues/{venueId}/?token="+ Constants.EVENTBRITE_TOKEN)
    Observable<Venue> getVenueById(@Path("venueId") String venueId);

    @GET("/v3/organizers/{organizerId}/?token="+ Constants.EVENTBRITE_TOKEN)
    Observable<Group> getOrganizerById(@Path("organizerId") String organizerId);
}
