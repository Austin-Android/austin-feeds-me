package com.example.utfeedsme.addeditevent;

import android.support.v7.app.AppCompatActivity;

import com.example.utfeedsme.addeditevent.AddEditEventContract.View;
import com.example.utfeedsme.data.EventsRepository;
import com.example.utfeedsme.data.FirebaseEventsDataSource;

/**
 * Created by darrankelinske on 4/13/16.
 */
public class AddEditEventActivity extends AppCompatActivity implements View {


    FirebaseEventsDataSource dataSource = FirebaseEventsDataSource.getInstance();

    EventsRepository repository = EventsRepository.getInstance(dataSource);



    @Override
    public void showEmptyTaskError() {

    }

    @Override
    public void showEventsList() {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setDescription(String description) {

    }
}
