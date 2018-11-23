package com.austindroids.austinfeedsme.data.meetup

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsDataSource
import com.austindroids.austinfeedsme.data.EventsRepository
import com.austindroids.austinfeedsme.data.Results
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*


/**
 * Created by daz on 8/26/16.
 */

class MeetupDataSource(val eventsRepository: EventsRepository) : EventsDataSource {

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback, onlyFood: Boolean) {

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        val meetupService = retrofit.create(MeetupService::class.java)

        meetupService.openEvents
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<Results>() {
                    override fun onSuccess(results: Results) {
                        val meetupEventMap = HashMap<String, Event>()
                        for (event in results.events) {
                            if (event.time > Date().time) {
                                meetupEventMap[event.id] = event
                            }
                        }

                        eventsRepository.getEvents(object: EventsDataSource.LoadEventsCallback {

                            override fun onEventsLoaded(events: MutableList<Event>?) {
                                events?.forEach {
                                    meetupEventMap.remove(it.id)
                                }

                                callback.onEventsLoaded(ArrayList(meetupEventMap.values))

                            }

                            override fun onError(error: String?) {
                                Timber.e(error)
                                callback.onEventsLoaded(ArrayList())
                            }
                        }, false)

                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        callback.onError(e.message)
                    }
                })
    }

    override fun saveEvent(eventToSave: Event?, callback: EventsDataSource.SaveEventCallback?) {

    }
}