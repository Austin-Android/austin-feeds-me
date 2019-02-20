package com.austindroids.austinfeedsme.di.modules

import com.austindroids.austinfeedsme.common.events.EventsContract
import com.austindroids.austinfeedsme.events.EventsActivity

import dagger.Binds
import dagger.Module

/**
 * Created by darrankelinske on 10/6/17.
 */

@Module
abstract class EventsPresenterModule {

    @Binds
    abstract fun view(eventsActivity: EventsActivity): EventsContract.View
}
