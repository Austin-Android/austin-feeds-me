package com.example.utfeedsme.addeditevent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.utfeedsme.addeditevent.AddEditEventContract.Presenter;
import com.example.utfeedsme.data.Event;
import com.example.utfeedsme.data.EventsDataSource;

import static com.parse.gdata.Preconditions.checkNotNull;

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
        mEventsRepository = checkNotNull(eventsRepository);
        mAddEditEventView = checkNotNull(addEventView);
    }

    @Override
    public void createEvent(String title, String description) {
        Event newEvent = new Event(title, description);
//        if (newEvent.isEmpty()) {
            // Oopsy - Need basic details
//        } else {
//            mEventsRepository.saveEvent(newEvent);
            mAddEditEventView.showEventsList();
//        }
    }
}
