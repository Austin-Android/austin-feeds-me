package com.austindroids.austinfeedsme.eventsfilter

import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsRepository
import com.austindroids.austinfeedsme.data.FilterableEventsRepository
import com.austindroids.austinfeedsme.di.modules.DataModule.Eventbrite
import com.austindroids.austinfeedsme.di.modules.DataModule.Meetup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by darrankelinske on 8/4/16.
 */
class EventFilterPresenter @Inject
constructor(private val filterableEventsRepository: FilterableEventsRepository, @param:Eventbrite private val eventbriteRepository: EventsRepository,
            @param:Meetup private val meetupRepository: EventsRepository, private val view: EventFilterContract.View) : EventFilterContract.Presenter {
    private var disposable: Disposable = Disposables.disposed()

    override fun loadEvents() {
        disposable = Observable.combineLatest(filterableEventsRepository.getEventsRX(true, false), eventbriteRepository.eventsRX, meetupRepository.eventsRX,
                { persistedEvents: List<Event>, eventbriteEvents: List<Event>, meetupEvents: List<Event> ->
                    val persistedIds = HashSet<String>()

                    for (event in persistedEvents) {
                        persistedIds.add(event.id)
                    }

                    val eventsToReturn = eventbriteEvents.filter {
                        !persistedIds.contains(it.id)
                    }.toMutableList()

                    eventsToReturn.addAll(meetupEvents)
                    eventsToReturn.sortedWith(compareBy { it.time })
                    eventsToReturn
                } as Function3<List<Event>, List<Event>, List<Event>, List<Event>>)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { events ->
                    view.showEvents(events)
                }
    }

    override fun dispose() {
        disposable.dispose()
    }
}