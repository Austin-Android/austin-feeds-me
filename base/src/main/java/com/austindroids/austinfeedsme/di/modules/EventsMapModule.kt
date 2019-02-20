package com.austindroids.austinfeedsme.di.modules

import com.austindroids.austinfeedsme.common.events.EventsContract
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity

import dagger.Binds
import dagger.Module

/**
 * Created by darrankelinske on 12/7/16.
 */

@Module
abstract class EventsMapModule {

    @Binds
    abstract fun view(mainActivity: EventsMapActivity): EventsContract.View

}