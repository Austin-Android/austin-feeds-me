package com.austindroids.austinfeedsme.data.meetup

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsDataSource
import com.austindroids.austinfeedsme.data.Results
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnCompleteListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import com.google.firebase.firestore.QuerySnapshot


/**
 * Created by daz on 8/26/16.
 */

class MeetupDataSource : EventsDataSource {

    internal val eventsReference = FirebaseFirestore.getInstance().collection("events")

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback) {

        val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()

        val meetupService = retrofit.create(MeetupService::class.java)

        meetupService.openEvents
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<Results>() {
                    override fun onSuccess(results: Results) {
                        val meetupEventMap = HashMap<String, Event>()
                        for (event in results.events) {
                            meetupEventMap[event.id] = event
                        }

                        eventsReference
                                .whereEqualTo("food", true)
                                .whereGreaterThan("time", java.util.Date().getTime())
                                .orderBy("time")
                                .get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                                    if (task.isSuccessful()) {
                                        for (snapshot in task.getResult()) {
                                            val event = snapshot.toObject(Event::class.java!!)
                                            meetupEventMap.remove(event?.id)
                                        }

                                        callback.onEventsLoaded(ArrayList(meetupEventMap.values))

                                    } else {
                                        Timber.e(task.getException())
                                    }
                                })
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }
                })

    }

    override fun saveEvent(eventToSave: Event, callback: EventsDataSource.SaveEventCallback) {

    }
}