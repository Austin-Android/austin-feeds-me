package com.austindroids.austinfeedsme.di.modules;

import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteDataSource;
import com.austindroids.austinfeedsme.data.firebase.FirebaseEventsDataSource;
import com.austindroids.austinfeedsme.data.meetup.MeetupDataSource;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module
public class DataModule {

    @Provides @Singleton
    CollectionReference firebase() {
        return FirebaseFirestore.getInstance().collection("events");
    }

    @Provides @Singleton
    FilterableEventsRepository filterableEventsRepository(FirebaseEventsDataSource firebaseEventsDataSource) {
        return new FilterableEventsRepository(firebaseEventsDataSource);
    }

    @Provides @Firebase @Singleton
    EventsRepository eventsRepository(FirebaseEventsDataSource eventsDataSource) {
        return new EventsRepository(eventsDataSource);
    }

    @Provides @Meetup @Singleton
    EventsDataSource meetupDataSource(@Firebase EventsRepository eventsRepository) {
        return new MeetupDataSource(eventsRepository);
    }

    @Provides @Eventbrite @Singleton
    EventsDataSource eventbriteDataSource(@Firebase EventsRepository eventsRepository) {
        return new EventbriteDataSource(eventsRepository);
    }

    @Provides @Meetup @Singleton
    EventsRepository meetupEventsRepository(@Meetup EventsDataSource meetupDataSource) {
        return new EventsRepository(meetupDataSource);
    }

    @Provides @Eventbrite @Singleton
    EventsRepository eventbriteEventsRepository(@Eventbrite EventsDataSource eventbriteDataSource) {
        return new EventsRepository(eventbriteDataSource);
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface Eventbrite {
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface Meetup {
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface Firebase {
    }
}
