package com.austindroids.austinfeedsme.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.common.utils.DateUtils
import com.austindroids.austinfeedsme.data.Event
import com.bumptech.glide.Glide

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

        var eventDescription = event.description ?: ""

        val result: Spanned
        result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(eventDescription, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(eventDescription)
        }

        event.group?.groupPhoto?.photoUrl?.run {
            Glide.with(context)
                    .load(this)
                    .into(viewHolder.groupPhotoImageView)
        }

        viewHolder.eventDate.text = DateUtils.getLocalDateFromTimestamp(event.time)
        viewHolder.title.text = event.name
        viewHolder.description.text = result
    }

    override fun getItemCount(): Int {
        return events!!.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(context).clear(holder.groupPhotoImageView)
        Glide.with(context).load(R.drawable.ic_local_pizza_blue_24dp).into(holder.groupPhotoImageView)
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

        var groupPhotoImageView: ImageView
        var eventDate: TextView
        var title: TextView
        var description: TextView
        private val pizzaIcon: ImageView
        private val beerIcon: ImageView
        private val tacoIcon: ImageView

        init {
            groupPhotoImageView = itemView.findViewById(R.id.image_view_group_photo) as ImageView
            eventDate = itemView.findViewById<View>(R.id.event_detail_time) as TextView
            title = itemView.findViewById<View>(R.id.event_detail_title) as TextView
            description = itemView.findViewById<View>(R.id.event_detail_description) as TextView
            pizzaIcon = itemView.findViewById<View>(R.id.event_pizza_icon) as ImageView
            beerIcon = itemView.findViewById<View>(R.id.event_beer_icon) as ImageView
            tacoIcon = itemView.findViewById<View>(R.id.event_taco_icon) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            val event = getItem(position)
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = Uri.parse(event.event_url)
            context.startActivity(webIntent)

            mItemListener.onEventClick(event)
        }
    }

    interface EventItemListener {
        fun onEventClick(clickedEvent: Event)
    }
}
