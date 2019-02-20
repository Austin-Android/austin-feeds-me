package com.austindroids.austinfeedsme.data;

import java.util.List;

import io.reactivex.Observable;

interface FilterableRxEventsDataSource extends RxEventsDataSource {
    Observable<List<Event>> getEventsRX(boolean onlyFuture, boolean onlyFood);
}
