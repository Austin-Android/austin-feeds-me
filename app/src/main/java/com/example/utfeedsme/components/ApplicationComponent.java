package com.example.utfeedsme.components;

import android.app.Application;

import com.example.utfeedsme.AllEvents;
import com.example.utfeedsme.addeditevent.AddEditEventActivity;
import com.example.utfeedsme.modules.AustinFeedsMeApplicationModule;
import com.example.utfeedsme.modules.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AustinFeedsMeApplicationModule.class, DataModule.class})
public interface ApplicationComponent {
    void inject(AddEditEventActivity addEditEventActivity);
    void inject(AllEvents allEventsActivity);

    // Exported for child-components.
    Application application();
}
