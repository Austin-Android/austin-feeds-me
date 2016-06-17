package com.example.utfeedsme.modules;

import com.example.utfeedsme.data.EventsDataSource;
import com.example.utfeedsme.data.FirebaseEventsDataSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {
    private static final String FIREBASE_URL = "https://austin-feeds-me.firebaseio.com/events";

    @Provides @Singleton
    DatabaseReference firebase() {
        return FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASE_URL);
    }

    @Provides @Singleton EventsDataSource eventsDataSource(FirebaseEventsDataSource firebaseEventsDataSource) {
        return firebaseEventsDataSource;
    }
}
