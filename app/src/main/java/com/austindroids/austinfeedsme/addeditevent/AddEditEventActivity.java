package com.austindroids.austinfeedsme.addeditevent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.austindroids.austinfeedsme.AustinFeedsMeApplication;
import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.addeditevent.AddEditEventContract.View;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsDataSource;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by darrankelinske on 4/13/16.
 */
public class AddEditEventActivity extends AppCompatActivity implements View {
    @Inject
    DatabaseReference firebase;
    @Inject EventsRepository repository;

    @Bind(R.id.title_add_edit_event_edittext)
    EditText title;
    @Bind(R.id.description_add_edit_event_edittext)
    EditText description;
    @Bind(R.id.host_add_edit_event_edittext)
    EditText host;
    @Bind(R.id.date_add_edit_event_edittext)
    EditText date;
    @Bind(R.id.rsvp_link_add_edit_event_edittext)
    EditText rsvpLink;
    @Bind(R.id.add_edit_event_linear_layout)
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AustinFeedsMeApplication) getApplication()).component().inject(this);
        setContentView(R.layout.activity_addeditevent);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_event_item:
                if (null != FirebaseAuth.getInstance().getCurrentUser()) {
                    repository.saveEvent(new Event(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            title.getText().toString(),
                            description.getText().toString(),
                            Long.parseLong(date.getText().toString()),
                            rsvpLink.getText().toString()),
                            new EventsDataSource.SaveEventCallback() {

                        @Override
                        public void onEventSaved(boolean success) {
                            Log.i("Woo", "Yay the event was saved: " + success);
                            finish();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                } else {
                    android.view.View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm =
                                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Snackbar.make(linearLayout,
                            "You must be logged in to save an event.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
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
