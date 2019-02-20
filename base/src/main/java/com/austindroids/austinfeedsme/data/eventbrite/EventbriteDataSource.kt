package com.austindroids.austinfeedsme.data.eventbrite

import com.austindroids.austinfeedsme.common.utils.EventbriteUtils
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsDataSource
import com.austindroids.austinfeedsme.data.EventsRepository
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


/**
 * Created by darrankelinske on 8/26/16.
 */
class EventbriteDataSource(val eventsRepository: EventsRepository) : EventsDataSource {

    private val observableList = ArrayList<Single<EventbriteEvents>>()

    init {
        val okHttpClient = OkHttpClient.Builder().build()

        val eventbriteRetrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://www.eventbriteapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        val eventbriteService = eventbriteRetrofit.create(EventbriteService::class.java)

        val searchList = arrayOf("taco", "pizza", "beer", "breakfast", "lunch", "dinner", "drinks", "spaghetti", "hamburger")

        for (searchTerm in searchList) {
            observableList.add(eventbriteService.getEventsByKeyword(searchTerm))
        }
    }

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback) {

        val eventIds: MutableSet<String> = mutableSetOf()

        val eventbriteEventsSubscriber = Consumer<List<EventbriteEvent>> { eventbriteEvents ->
            val convertedEventbriteEvents = ArrayList<Event>()
            for (eventbriteEvent in eventbriteEvents) {
                convertedEventbriteEvents.add(EventbriteUtils.transformEventBrite(eventbriteEvent))
            }
            callback.onEventsLoaded(convertedEventbriteEvents)
        }

        val listMerger = Function<Array<Any>, List<EventbriteEvent>> { eventBriteEvents ->

            val mergedEvents: ArrayList<EventbriteEvent> = ArrayList()

            eventBriteEvents.map { it as EventbriteEvents }.forEach { eventbriteEvents ->
                eventbriteEvents.events.forEach {
                    if (!eventIds.contains(it.id)) {
                        eventIds.add(it.id)
                        mergedEvents.add(it)
                    }
                }
            }

            return@Function mergedEvents

        }

        Single.zip(observableList, listMerger)
                .subscribe(eventbriteEventsSubscriber, Consumer<Throwable> { Timber.e(it) })
    }

    override fun saveEvent(eventToSave: Event, callback: EventsDataSource.SaveEventCallback) {

    }
}
