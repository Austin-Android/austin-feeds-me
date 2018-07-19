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

package com.austindroids.austinfeedsme.data

import javax.inject.Singleton

@Singleton
class EventsRepository(private val eventsRemoteDataSource: EventsDataSource) : EventsDataSource {

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback, onlyFood: Boolean) {
        eventsRemoteDataSource.getEvents(object : EventsDataSource.LoadEventsCallback {
            override fun onEventsLoaded(events: List<Event>) {
                callback?.onEventsLoaded(events)
            }

            override fun onError(error: String) {
                callback?.onError(error)
            }
        }, onlyFood)

    }

    override fun saveEvent(eventToSave: Event, callback: EventsDataSource.SaveEventCallback?) {
        eventsRemoteDataSource.saveEvent(eventToSave, object : EventsDataSource.SaveEventCallback {
            override fun onEventSaved(success: Boolean) {
                callback?.onEventSaved(success)
            }

            override fun onError(error: String) {
                callback?.onError(error)
            }
        })

    }
}
