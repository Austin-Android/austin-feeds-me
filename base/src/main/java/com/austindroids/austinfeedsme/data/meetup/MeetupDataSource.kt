package com.austindroids.austinfeedsme.data.meetup

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsDataSource
import com.austindroids.austinfeedsme.data.Results
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*


/**
 * Created by daz on 8/26/16.
 */

class MeetupDataSource : EventsDataSource {

    internal var database = FirebaseDatabase.getInstance()
    internal val eventsReference = database.getReference("events")

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
                        eventsReference.orderByChild("time").startAt(Date().time.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (postSnapshot in dataSnapshot.children) {
                                    val firebaseEvent = postSnapshot.getValue(Event::class.java)
                                    meetupEventMap.remove(firebaseEvent!!.id)
                                }

                                Timber.d("After cleaning we have this many events: %s", meetupEventMap.size)
                                if (meetupEventMap.size != 0) {
                                    callback.onEventsLoaded(ArrayList(meetupEventMap.values))
                                }

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Timber.e(databaseError.toException())
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