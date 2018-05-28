package com.austindroids.austinfeedsme.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.utility.DateUtils

/**
 * Created by darrankelinske on 12/3/16.
 */
internal class EventsAdapter(private val context: Context, Events: List<Event>,
                             private val eventClickListener: EventItemListener) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    var events: List<Event>? = null
        private set

    init {
        setList(Events)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val eventView = inflater.inflate(R.layout.item_event, parent, false)

        return ViewHolder(eventView, eventClickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = getItem(position)

        val result: Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(event.description, Html.FROM_HTML_MODE_LEGACY)
        } else {
            result = Html.fromHtml(event.description)
        }

        viewHolder.eventDate.text = DateUtils.getLocalDateFromTimestamp(event.time)
        viewHolder.title.text = event.name
        viewHolder.description.text = result
        viewHolder.eventUrl.movementMethod = LinkMovementMethod.getInstance()
        viewHolder.eventUrl.setOnClickListener(View.OnClickListener {
            val eventUrl = event.event_url ?: return@OnClickListener
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = Uri.parse(eventUrl)
            context.startActivity(webIntent)
        })
    }

    override fun getItemCount(): Int {
        return events!!.size
    }

    fun replaceData(Events: List<Event>) {
        setList(Events)
        notifyDataSetChanged()
    }

    private fun setList(Events: List<Event>) {
        events = Events
    }

    private fun getItem(position: Int): Event {
        return events!![position]
    }

    inner class ViewHolder(itemView: View, private val mItemListener: EventItemListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var eventDate: TextView
        var title: TextView
        var description: TextView
        var eventUrl: Button
        private val pizzaIcon: ImageView
        private val beerIcon: ImageView
        private val tacoIcon: ImageView

        init {
            eventDate = itemView.findViewById<View>(R.id.event_detail_time) as TextView
            title = itemView.findViewById<View>(R.id.event_detail_title) as TextView
            description = itemView.findViewById<View>(R.id.event_detail_description) as TextView
            pizzaIcon = itemView.findViewById<View>(R.id.event_pizza_icon) as ImageView
            beerIcon = itemView.findViewById<View>(R.id.event_beer_icon) as ImageView
            tacoIcon = itemView.findViewById<View>(R.id.event_taco_icon) as ImageView
            eventUrl = itemView.findViewById<View>(R.id.event_link) as Button
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            val Event = getItem(position)
            mItemListener.onEventClick(Event)

        }
    }

    interface EventItemListener {
        fun onEventClick(clickedEvent: Event)
    }
}
