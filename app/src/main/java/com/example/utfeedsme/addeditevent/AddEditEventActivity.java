package com.example.utfeedsme.addeditevent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.utfeedsme.R;
import com.example.utfeedsme.addeditevent.AddEditEventContract.View;
import com.example.utfeedsme.data.Event;
import com.example.utfeedsme.data.EventsDataSource;
import com.example.utfeedsme.data.EventsRepository;
import com.example.utfeedsme.data.FirebaseEventsDataSource;
import com.firebase.client.Firebase;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by darrankelinske on 4/13/16.
 */
public class AddEditEventActivity extends AppCompatActivity implements View {

    @Bind(R.id.title_addeditevent_edittext)
    EditText title;
    @Bind(R.id.description_addeditevent_edittext)
    EditText description;
    @Bind(R.id.save_addeditevent_button)
    Button saveButton;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addeditevent);
        ButterKnife.bind(this);

        final Firebase ref = new Firebase("https://austin-feeds-me.firebaseio.com/");

        FirebaseEventsDataSource dataSource = FirebaseEventsDataSource.getInstance();
        final EventsRepository repository = EventsRepository.getInstance(dataSource);

        saveButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                repository.saveEvent(new Event(ref.getAuth().getUid(), title.getText().toString(),
                        description.getText().toString()), new EventsDataSource.SaveEventCallback() {
                    @Override
                    public void onEventSaved(boolean success) {
                        Log.i("Woo", "Yay the event was saved: "+success);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });

    }





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
