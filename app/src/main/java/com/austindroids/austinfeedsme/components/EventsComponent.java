package com.austindroids.austinfeedsme.components;

import com.austindroids.austinfeedsme.PerActivity;
import com.austindroids.austinfeedsme.events.EventsActivity;
import com.austindroids.austinfeedsme.eventsmap.EventsMapActivity;
import com.austindroids.austinfeedsme.modules.EventsPresenterModule;

import dagger.Component;

/**
 * Created by darrankelinske on 12/7/16.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = EventsPresenterModule.class)
public interface EventsComponent {
    void inject(EventsActivity activity);
    void inject(EventsMapActivity activity);
}