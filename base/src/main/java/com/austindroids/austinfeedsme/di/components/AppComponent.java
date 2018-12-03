package com.austindroids.austinfeedsme.di.components;

import android.app.Application;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;
import com.austindroids.austinfeedsme.di.modules.ActivityBindingModule;
import com.austindroids.austinfeedsme.di.modules.ApplicationModule;
import com.austindroids.austinfeedsme.di.modules.DataModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ActivityBindingModule.class,
        ApplicationModule.class, DataModule.class})
public interface AppComponent extends AndroidInjector<AustinFeedsMeApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<AustinFeedsMeApplication> {

        @BindsInstance
        public abstract Builder application(Application application);

        public abstract Builder dataModule(DataModule datassModule);

        public abstract AppComponent build();
    }

    EventsRepository getEventsRepository();
    FilterableEventsRepository getFilterableEventsRepository();
}
