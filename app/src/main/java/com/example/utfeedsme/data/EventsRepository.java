/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.utfeedsme.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Concrete implementation to load Events from the a data source.
 */
public class EventsRepository implements EventsDataSource {

    private static EventsRepository INSTANCE = null;

    private final EventsDataSource mEventsRemoteDataSource;

    private EventsRepository(@NonNull EventsDataSource eventsRemoteDataSource) {
        mEventsRemoteDataSource = eventsRemoteDataSource;
    }

    public static EventsRepository getInstance(EventsDataSource eventssRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new EventsRepository(eventssRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {

        mEventsRemoteDataSource.getEvents(new LoadEventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                callback.onEventsLoaded(events);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });

    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {

    }
}
