package com.austindroids.austinfeedsme.addeditevent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.austindroids.austinfeedsme.addeditevent.AddEditEventContract.Presenter;
import com.austindroids.austinfeedsme.data.EventsDataSource;


/**
 * Created by darrankelinske on 4/13/16.
 */
public class AddEditEventPresenter implements Presenter {

    private String mEventId;
    private EventsDataSource mEventsRepository;
    private AddEditEventContract.View mAddEditEventView;

    public AddEditEventPresenter(@Nullable String taskId, @NonNull EventsDataSource eventsRepository,
                                @NonNull AddEditEventContract.View addEventView) {
        mEventId = taskId;
        mEventsRepository = eventsRepository;
        mAddEditEventView = addEventView;
    }

    @Override
    public void createEvent(String uid, String title, String description) {

            mAddEditEventView.showEventsList();

    }
}
