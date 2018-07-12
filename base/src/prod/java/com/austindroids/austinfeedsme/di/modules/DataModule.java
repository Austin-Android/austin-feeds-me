package com.austindroids.austinfeedsme.di.modules;

import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteDataSource;
import com.austindroids.austinfeedsme.data.firebase.FirebaseEventsDataSource;
import com.austindroids.austinfeedsme.data.meetup.MeetupDataSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {
    private static final String FIREBASE_URL = "https://austin-feeds-me.firebaseio.com/events";

    @Provides @Singleton
    CollectionReference firebase() {
        return FirebaseFirestore.getInstance().collection("events");
    }

    @Provides @Singleton
    EventsRepository eventsRepository(FirebaseEventsDataSource firebaseEventsDataSource) {
        return new EventsRepository(firebaseEventsDataSource);
    }

    @Provides @Named("meetup") @Singleton
    EventsDataSource meetupDataSource() {
        return new MeetupDataSource();
    }

    @Provides @Named("eventbrite") @Singleton
    EventsDataSource eventbriteDataSource() {
        return new EventbriteDataSource();
    }
}
