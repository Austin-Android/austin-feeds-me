package com.austindroids.austinfeedsme.addeditevent

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.addeditevent.AddEditEventContract.View
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsDataSource
import com.austindroids.austinfeedsme.data.EventsRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * Created by darrankelinske on 4/13/16.
 */
class AddEditEventActivity : AppCompatActivity(), View {

    @Inject lateinit var repository: EventsRepository

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var host: EditText
    private lateinit var date: EditText
    private lateinit var rsvpLink: EditText
    private lateinit var foodType: EditText
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addeditevent)

        title = findViewById(R.id.title_add_edit_event_edittext)
        description = findViewById(R.id.description_add_edit_event_edittext)
        host = findViewById(R.id.date_add_edit_event_edittext)
        date = findViewById(R.id.date_add_edit_event_edittext)
        rsvpLink = findViewById(R.id.rsvp_link_add_edit_event_edittext)
        foodType = findViewById(R.id.food_type_add_edit_event_edittext)
        linearLayout = findViewById(R.id.add_edit_event_linear_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.add_event_item -> {
                if (null != FirebaseAuth.getInstance().currentUser) {
                    repository!!.saveEvent(Event(FirebaseAuth.getInstance().currentUser!!.uid,
                            title!!.text.toString(),
                            description!!.text.toString(),
                            java.lang.Long.parseLong(date!!.text.toString()),
                            rsvpLink!!.text.toString(),
                            foodType!!.text.toString()),
                            object : EventsDataSource.SaveEventCallback {

                                override fun onEventSaved(success: Boolean) {
                                    Log.i("Woo", "Yay the event was saved: $success")
                                    finish()
                                }

                                override fun onError(error: String) {

                                }
                            })
                } else {
                    val view = this.currentFocus
                    if (view != null) {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    Snackbar.make(linearLayout!!,
                            "You must be logged in to save an event.", Snackbar.LENGTH_SHORT)
                            .show()
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showEmptyTaskError() {

    }

    override fun showEventsList() {

    }

    override fun setTitle(title: String) {

    }

    override fun setDescription(description: String) {

    }
}
