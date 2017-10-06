package com.austindroids.austinfeedsme.di.modules;

import com.austindroids.austinfeedsme.common.EventsContract;
import com.austindroids.austinfeedsme.common.EventsPresenter;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by darrankelinske on 12/7/16.
 */

@ActivityScoped
@Module
public abstract class EventsMapPresenterModule {

    @Binds
    public abstract EventsContract.View view(EventsMapActivity mainActivity);

    @Provides
    static EventsContract.Presenter provideEventsPresenter(EventsRepository repository,
                                                           EventsContract.View view) {
        return new EventsPresenter(repository, view);
    }

}