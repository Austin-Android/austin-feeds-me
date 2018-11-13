package com.austindroids.austinfeedsme.eventsmap

import android.content.Intent
import android.net.Uri
import androidx.viewpager.widget.PagerAdapter
import androidx.cardview.widget.CardView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.data.Event

/**
 * Created by pauljoiner on 9/11/16.
 */
class CardPagerAdapter(private val events: List<Event>) : PagerAdapter() {
    private var mBaseElevation: Float = 0.toFloat()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.adapter, container, false)
        container.addView(view)
        val cardView = view.findViewById<CardView>(R.id.cardView)
        val titleText = view.findViewById<TextView>(R.id.card_title_text)
        val bodyText = view.findViewById<TextView>(R.id.card_body_text)
        val rsvpButton = view.findViewById<Button>(R.id.card_rsvp_button)
        rsvpButton.setOnClickListener { view1 ->
            val rsvpLink = events[position].event_url
            if (rsvpLink != null) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(rsvpLink)
                view1.context.startActivity(i)
            }
        }

        titleText.text = events[position].name
        bodyText.text = events[position].description
        bodyText.movementMethod = ScrollingMovementMethod()

        if (mBaseElevation == 0f) {
            mBaseElevation = cardView.cardElevation
        }

        cardView.maxCardElevation = mBaseElevation * MAX_ELEVATION_FACTOR
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return events.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun getEventAtPosition(position: Int): Event {
        return events[position]
    }

    companion object {

        private const val MAX_ELEVATION_FACTOR = 8
    }
}
