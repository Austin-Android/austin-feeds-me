package com.example.utfeedsme.modules;

import com.example.utfeedsme.data.EventsDataSource;
import com.example.utfeedsme.data.FirebaseEventsDataSource;
import com.firebase.client.Firebase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {
    private static final String FIREBASE_URL = "https://austin-feeds-me.firebaseio.com/events";

    @Provides @Singleton Firebase firebase() {
        return new Firebase(FIREBASE_URL);
    }

    @Provides @Singleton EventsDataSource eventsDataSource(FirebaseEventsDataSource firebaseEventsDataSource) {
        return firebaseEventsDataSource;
    }
}
