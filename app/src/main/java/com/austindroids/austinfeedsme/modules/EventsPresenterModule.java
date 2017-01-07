package com.austindroids.austinfeedsme.modules;

import com.austindroids.austinfeedsme.PerActivity;
import com.austindroids.austinfeedsme.common.EventsContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by darrankelinske on 12/7/16.
 */

@PerActivity
@Module
public class EventsPresenterModule {

    private final EventsContract.View mView;

    public EventsPresenterModule(EventsContract.View view) {
        mView = view;
    }

    @Provides
    EventsContract.View provideEventsContractView() {
        return mView;
    }

}