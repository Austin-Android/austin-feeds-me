package com.austindroids.austinfeedsme.data.meetup;

import com.austindroids.austinfeedsme.common.Constants;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.Results;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by darrankelinske on 8/4/16.
 */
public interface MeetupService {

    @GET("/2/open_events?key="+ Constants.MEETUP_KEY+"" +
            "&sign=true" +
            "&and_text=False" +
            "&photo-host=public" +
            "&zip=78701" +
            "&pizza+provided+beer+breakfast+lunch+dinner+taco" +
            "&page=100" +
            "&status=upcoming" +
            "&radius=40")
    Single<Results> getOpenEvents();
}