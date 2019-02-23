package com.austindroids.austinfeedsme.common.events

import android.text.TextUtils

import com.austindroids.austinfeedsme.common.utils.DateUtils
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.FilterableEventsRepository
import com.austindroids.austinfeedsme.di.scopes.ActivityScoped
import com.austindroids.austinfeedsme.events.EventsFilterType

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import javax.inject.Inject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

import com.austindroids.austinfeedsme.data.Event.Type.BEER
import com.austindroids.austinfeedsme.data.Event.Type.NONE
import com.austindroids.austinfeedsme.data.Event.Type.PIZZA
import com.austindroids.austinfeedsme.data.Event.Type.TACO

@ActivityScoped
class EventsPresenter @Inject
constructor(private val repository: FilterableEventsRepository, private val view: EventsContract.View) : EventsContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private var currentFiltering = EventsFilterType.ALL_EVENTS

    override fun loadEvents() {
        loadEvents(true)
    }

    private fun loadEvents(showProgress: Boolean) {
        val eventDisposable = repository.getEventsRX(true, true)
                .doOnSubscribe {
                    if (showProgress) {
                        view.showProgress()
                    }
                }.doOnError { throwable ->
                    view.hideProgress()
                    Timber.e(throwable)
                }.subscribe(Consumer { events ->
                    if (events.isEmpty()) {
                        view.showNoEventsView()
                        view.hideProgress()
                        return@Consumer
                    }

                    val eventsToShow = filterEventsOnTimeSelection(events)

                    view.hideProgress()
                    view.showEvents(eventsToShow)
                    setEventCounts(eventsToShow)
                })

        compositeDisposable.add(eventDisposable)
    }


    override fun refreshEvents() {
        loadEvents(false)
    }

    override fun searchEvents(searchTerm: String) {
        if (searchTerm.equals(SEARCH_RESET_STRING, ignoreCase = true)) {
            loadEvents()
            return
        }

        val lowerCaseSearch = searchTerm.toLowerCase()

        val subscribe = repository.eventsRX
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ events ->
                    val filteredEvents = events.filter {event ->
                        !event.isFood
                                || event.time < Date().time
                                || TextUtils.isEmpty(event.description)
                                || !event.description.toLowerCase().contains(lowerCaseSearch)
                    }

                    view.showEvents(filteredEvents)
                }, { throwable -> Timber.e(Exception(throwable)) })
    }

    override fun setFiltering(eventsFilterType: EventsFilterType) {
        currentFiltering = eventsFilterType
    }

    private fun filterEventsOnTimeSelection(events: List<Event>): ArrayList<Event> {
        val aMinuteFromMidnight = DateUtils.aMinuteFromMinuteToday()
        val sevenDaysFromNow = DateUtils.sevenDaysFromNow()

        val eventsToShow = ArrayList<Event>()
        for (event in events) {
            when (currentFiltering) {
                EventsFilterType.ALL_EVENTS -> eventsToShow.add(event)
                EventsFilterType.TODAYS_EVENTS -> if (event.time < aMinuteFromMidnight) {
                    eventsToShow.add(event)
                }
                EventsFilterType.THIS_WEEKS_EVENTS -> if (event.time < sevenDaysFromNow) {
                    eventsToShow.add(event)
                }
                else -> eventsToShow.add(event)
            }
        }
        return eventsToShow
    }

    private fun setEventCounts(eventsToShow: ArrayList<Event>) {
        view.setTotalCount(eventsToShow.size)

        val yummyCounts = HashMap<String, Int>()
        for (event in eventsToShow) {

            if (event.foodType == null || event.foodType == NONE.toString()) {
                continue
            }

            if (event.foodType == PIZZA.name) {
                val previousValue = yummyCounts[PIZZA.name]
                yummyCounts[PIZZA.name] = if (previousValue == null) 1 else previousValue + 1
            }
            if (event.foodType == BEER.name) {
                val previousValue = yummyCounts[BEER.name]
                yummyCounts[BEER.name] = if (previousValue == null) 1 else previousValue + 1
            }
            if (event.foodType == TACO.name) {
                val previousValue = yummyCounts[TACO.name]
                yummyCounts[TACO.name] = if (previousValue == null) 1 else previousValue + 1
            }

        }

        view.setPizzaCount(yummyCounts[PIZZA.name] ?: 0)
        view.setTacoCount(yummyCounts[TACO.name] ?: 0)
        view.setBeerCount(yummyCounts[BEER.name] ?: 0)
    }

    companion object {
        const val SEARCH_RESET_STRING = "reset"
    }
}