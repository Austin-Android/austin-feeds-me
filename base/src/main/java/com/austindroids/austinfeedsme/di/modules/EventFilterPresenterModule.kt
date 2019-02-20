package com.austindroids.austinfeedsme.di.modules

import com.austindroids.austinfeedsme.eventsfilter.EventFilterActivity
import com.austindroids.austinfeedsme.eventsfilter.EventFilterContract
import dagger.Binds
import dagger.Module

@Module
abstract class EventFilterPresenterModule {

    @Binds
    abstract fun view(mainActivity: EventFilterActivity): EventFilterContract.View

}