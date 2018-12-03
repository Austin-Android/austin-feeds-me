package com.austindroids.austinfeedsme.di.modules;

import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteDataSource;
import com.austindroids.austinfeedsme.data.firebase.FirebaseEventsDataSource;
import com.austindroids.austinfeedsme.data.meetup.MeetupDataSource;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides @Singleton
    EventsRepository eventsRepository(FirebaseEventsDataSource eventsDataSource) {
        return new EventsRepository(eventsDataSource);
    }

    @Provides @Named("meetup") @Singleton
    EventsDataSource meetupDataSource(EventsRepository eventsRepository) {
        return new MeetupDataSource(eventsRepository);
    }

    @Provides @Named("eventbrite") @Singleton
    EventsDataSource eventbriteDataSource(EventsRepository eventsRepository) {
        return new EventbriteDataSource(eventsRepository);
    }
}
