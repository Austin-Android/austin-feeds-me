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

package com.austindroids.austinfeedsme.data;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public class EventsRepository implements EventsDataSource {
    private final EventsDataSource eventsRemoteDataSource;

    public EventsRepository(EventsDataSource eventsDataSource) {
        this.eventsRemoteDataSource = eventsDataSource;
    }

    @Override
    public void getEvents(final LoadEventsCallback callback) {
        eventsRemoteDataSource.getEvents(new LoadEventsCallback() {
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
    public void saveEvent(Event eventToSave, final SaveEventCallback callback) {
        eventsRemoteDataSource.saveEvent(eventToSave, new SaveEventCallback() {
            @Override
            public void onEventSaved(boolean success) {
                callback.onEventSaved(success);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });

    }
}
