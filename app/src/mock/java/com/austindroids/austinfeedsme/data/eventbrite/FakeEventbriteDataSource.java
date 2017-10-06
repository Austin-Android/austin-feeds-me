/*
 * Copyright 2016, The Android Open Source Project
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

package com.austindroids.austinfeedsme.data.eventbrite;

import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeEventbriteDataSource implements EventsDataSource {

    private static final Map<String, Event> EVENTS_SERVICE_DATA = new LinkedHashMap<>();

    public FakeEventbriteDataSource() {

        // We need to create groups for events used in FakeEventbriteDataSource

        Event testEventOne = new Event("1", "Pizza Fest", "Pizza Everywhere", 33928672270000L,
                "www.pizza.com", "pizza", true);
        Event testEventTwo = new Event("2", "Beer", "Duff Everywhere", 33928672270777L,
                "www.duffman.com", "beer", true);

        EVENTS_SERVICE_DATA.put(testEventOne.getId(), testEventOne);
        EVENTS_SERVICE_DATA.put(testEventTwo.getId(), testEventTwo);
    }

    @Override
    public void getEvents(LoadEventsCallback callback) {
        List<Event> eventList = new ArrayList<>(EVENTS_SERVICE_DATA.values());
        callback.onEventsLoaded(eventList);
    }

    @Override
    public void getEvent(String eventId, LoadEventCallback callback) {
        Event event = EVENTS_SERVICE_DATA.get(eventId);
        callback.onEventLoaded(event);
    }

    @Override
    public void saveEvent(Event eventToSave, SaveEventCallback callback) {
        EVENTS_SERVICE_DATA.put(eventToSave.getId(), eventToSave);
        callback.onEventSaved(true);
    }
}
