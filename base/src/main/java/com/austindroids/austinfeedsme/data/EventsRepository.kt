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

import io.reactivex.Observable
import io.reactivex.Single

open class EventsRepository(private val eventsRemoteDataSource: EventsDataSource) : RxEventsDataSource {

    override fun getEventsRX(): Observable<List<Event>>? {
        return Observable.create {
            eventsRemoteDataSource.getEvents(object : EventsDataSource.LoadEventsCallback {
                override fun onEventsLoaded(events: List<Event>) {
                    it.onNext(events)
                }

                override fun onError(error: String) {
                    it.onError(Throwable(error))
                }
            })
        }
    }

    override fun saveEventRX(eventToSave: Event?): Single<Boolean> {
        return Single.create {
            eventsRemoteDataSource.saveEvent(eventToSave, object : EventsDataSource.SaveEventCallback {
                override fun onEventSaved(success: Boolean) {
                    it.onSuccess(true)
                }

                override fun onError(error: String) {
                    it.onError(Throwable(error))
                }
            })
        }
    }
}