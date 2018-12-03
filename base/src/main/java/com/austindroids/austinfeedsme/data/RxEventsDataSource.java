package com.austindroids.austinfeedsme.data;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface RxEventsDataSource {
    Observable<List<Event>> getEventsRX();
    Single<Boolean> saveEventRX(Event eventToSave);
}
