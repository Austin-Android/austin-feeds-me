package com.austindroids.austinfeedsme.data

import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class FilterableEventsRepository(private val eventsRemoteDataSource: FilterableEventDataSource)
    : EventsRepository(eventsRemoteDataSource), FilterableRxEventsDataSource {

    override fun getEventsRX(onlyFuture: Boolean, onlyFood: Boolean): Observable<List<Event>> {
        return Observable.create {
            eventsRemoteDataSource.getEvents(object : EventsDataSource.LoadEventsCallback {
                override fun onEventsLoaded(events: List<Event>) {
                    it.onNext(events)
                }

                override fun onError(error: String) {
                    it.onError(Throwable(error))
                }
            }, onlyFuture, onlyFood)
        }
    }
}