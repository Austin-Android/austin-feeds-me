package com.austindroids.austinfeedsme.components;

import android.app.Application;

import com.austindroids.austinfeedsme.addeditevent.AddEditEventActivity;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.eventsfilter.EventFilterActivity;
import com.austindroids.austinfeedsme.modules.ApplicationModule;
import com.austindroids.austinfeedsme.modules.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DataModule.class})
public interface ApplicationComponent {
    void inject(AddEditEventActivity addEditEventActivity);
    void inject(EventFilterActivity eventFilterActivity);

    // Exported for child-components.
    Application application();
    EventsRepository getEventsRepository();
}
