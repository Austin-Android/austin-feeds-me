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


/**
 * Created by daz on 8/26/16.
 */

class MeetupDataSource(val eventsRepository: EventsRepository) : EventsDataSource {

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.meetup.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()!!

    private val meetupService = retrofit.create(MeetupService::class.java)!!

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback, onlyFood: Boolean) {

        meetupService.openEvents
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<Results>() {
                    override fun onSuccess(results: Results) {
                        callback.onEventsLoaded(results.events)
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