package com.austindroids.austinfeedsme.common.events;

import android.text.TextUtils;

import com.austindroids.austinfeedsme.common.utils.DateUtils;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.FilterableEventsRepository;
import com.austindroids.austinfeedsme.di.scopes.ActivityScoped;
import com.austindroids.austinfeedsme.events.EventsFilterType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.austindroids.austinfeedsme.data.Event.Type.BEER;
import static com.austindroids.austinfeedsme.data.Event.Type.NONE;
import static com.austindroids.austinfeedsme.data.Event.Type.PIZZA;
import static com.austindroids.austinfeedsme.data.Event.Type.TACO;

@ActivityScoped
public class EventsPresenter implements EventsContract.Presenter {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FilterableEventsRepository repository;
    private EventsContract.View view;

    private EventsFilterType currentFiltering = EventsFilterType.ALL_EVENTS;

    @Inject
    public EventsPresenter(FilterableEventsRepository repository, EventsContract.View view) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void loadEvents() {
        loadEvents(true);
    }

    private void loadEvents(boolean showProgress) {
        Disposable eventDisposable = repository.getEventsRX(true, true)
                .doOnSubscribe(disposable -> {
                    if (showProgress) {
                        view.showProgress();
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.hideProgress();
                        Timber.e(throwable);
                    }
                }).subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(List<Event> events) throws Exception {
                        if (events.isEmpty()) {
                            view.showNoEventsView();
                            view.hideProgress();
                            return;
                        }

                        ArrayList<Event> eventsToShow = filterEventsOnTimeSelection(events);

                        view.hideProgress();
                        view.showEvents(eventsToShow);
                        setEventCounts(eventsToShow);
                    }
                });

        compositeDisposable.add(eventDisposable);
    }



    @Override
    public void refreshEvents() {
        loadEvents(false);
    }

    @Override
    public void searchEvents(final String searchTerm) {
        if (searchTerm.equalsIgnoreCase("reset")) {
            loadEvents();
            return;
        }

        final String lowerCaseSearch = searchTerm.toLowerCase();

        repository.getEventsRX()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(List<Event> events) throws Exception {
                        Iterator<Event> iterator = events.iterator();

                        while (iterator.hasNext()) {
                            Event nextEvent = iterator.next();

                            // Remove event if it doesn't have free food or is in the past
                            // or if the event name or description doesn't contain the search term
                            if (!nextEvent.isFood()
                                    || (nextEvent.getTime() < new Date().getTime())
                                    || TextUtils.isEmpty(nextEvent.getDescription())
                                    || !nextEvent.getDescription().toLowerCase().contains(lowerCaseSearch)) {
                                iterator.remove();
                            }
                        }

                        view.showEvents(events);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(new Exception(throwable));

                    }
                });
    }

    @Override
    public void setFiltering(EventsFilterType eventsFilterType) {
        currentFiltering = eventsFilterType;
    }

    @NonNull
    private ArrayList<Event> filterEventsOnTimeSelection(List<Event> events) {
        long aMinuteFromMidnight = DateUtils.INSTANCE.aMinuteFromMinuteToday();
        long sevenDaysFromNow = DateUtils.INSTANCE.sevenDaysFromNow();

        ArrayList<Event> eventsToShow = new ArrayList<>();
        for (Event event : events) {
            switch (currentFiltering) {
                case ALL_EVENTS:
                    eventsToShow.add(event);
                    break;
                case TODAYS_EVENTS:
                    if (event.getTime() < aMinuteFromMidnight) {
                        eventsToShow.add(event);
                    }
                    break;
                case THIS_WEEKS_EVENTS:
                    if (event.getTime() < sevenDaysFromNow) {
                        eventsToShow.add(event);
                    }
                    break;
                default:
                    eventsToShow.add(event);
                    break;
            }
        }
        return eventsToShow;
    }

    private void setEventCounts(ArrayList<Event> eventsToShow) {
        view.setTotalCount(eventsToShow.size());

        final HashMap<String, Integer> yummyCounts = new HashMap<>();
        for (Event event : eventsToShow) {

            if (event.getFoodType() == null || event.getFoodType().equals(NONE.toString())) {
                continue;
            }

            if (event.getFoodType().equals(PIZZA.name())) {
                Integer previousValue = yummyCounts.get(PIZZA.name());
                yummyCounts.put(PIZZA.name(), previousValue == null ? 1 : previousValue + 1);
            }
            if (event.getFoodType().equals(BEER.name())) {
                Integer previousValue = yummyCounts.get(BEER.name());
                yummyCounts.put(BEER.name(), previousValue == null ? 1 : previousValue + 1);
            }
            if (event.getFoodType().equals(TACO.name())) {
                Integer previousValue = yummyCounts.get(TACO.name());
                yummyCounts.put(TACO.name(), previousValue == null ? 1 : previousValue + 1);
            }

        }

        view.setPizzaCount(yummyCounts.get(PIZZA.name()) == null ? 0 : yummyCounts.get(PIZZA.name()));
        view.setTacoCount(yummyCounts.get(TACO.name()) == null ? 0 : yummyCounts.get(TACO.name()));
        view.setBeerCount(yummyCounts.get(BEER.name()) == null ? 0 : yummyCounts.get(BEER.name()));
    }
}