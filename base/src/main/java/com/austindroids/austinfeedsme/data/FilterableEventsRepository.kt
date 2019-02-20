package com.austindroids.austinfeedsme.data

import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FilterableEventsRepository @Inject constructor(private val eventsRemoteDataSource: FilterableEventDataSource)
    : EventsRepository(eventsRemoteDataSource), FilterableRxEventsDataSource {

    override fun getEventsRX(onlyFuture: Boolean, onlyFood: Boolean): Observable<List<Event>> {
        return Observable.create { emitter ->
            eventsRemoteDataSource.getEvents(object : EventsDataSource.LoadEventsCallback {
                override fun onEventsLoaded(events: List<Event>) {
                    emitter.onNext(events)
                }

                override fun onError(error: String) {
                    emitter.onError(Throwable(error))
                }
            }, onlyFuture, onlyFood)
        }
    }
}