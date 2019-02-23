package com.austindroids.austinfeedsme.eventsfilter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.austindroids.austinfeedsme.R
import com.austindroids.austinfeedsme.data.Event
import com.austindroids.austinfeedsme.data.EventsRepository

import java.util.ArrayList

import androidx.recyclerview.widget.RecyclerView


import com.austindroids.austinfeedsme.data.Event.Type.BEER
import com.austindroids.austinfeedsme.data.Event.Type.NONE
import com.austindroids.austinfeedsme.data.Event.Type.PIZZA
import com.austindroids.austinfeedsme.data.Event.Type.TACO
import com.austindroids.austinfeedsme.eventsfilter.EventFilterAdapter.EventViewHolder


class EventFilterAdapter(private val eventsRepository: EventsRepository) : RecyclerView.Adapter<EventViewHolder>() {
    private var events: List<Event> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val quoteView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_event_filter, parent, false)
        return EventViewHolder(quoteView)
    }

    override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int) {
        viewHolder.bindEvent(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun setEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var event: Event
        private val titleTextView: TextView = itemView.findViewById(R.id.event_title)
        private val groupTextView: TextView = itemView.findViewById(R.id.event_group_name)
        private val eventLinkTextView: TextView = itemView.findViewById(R.id.event_link)
        private val addEventButton: Button = itemView.findViewById(R.id.button_add_event)
        private val removeEventButton: Button = itemView.findViewById(R.id.button_remove_event)

        init {

            addEventButton.setOnClickListener {
                event.isFood = true
                if (event.description != null) {
                    val stringToTest: String = event.description.toUpperCase()
                    when {
                        stringToTest.contains(PIZZA.name) -> event.foodType = PIZZA.name
                        stringToTest.contains(BEER.name) -> event.foodType = BEER.name
                        stringToTest.contains(TACO.name) -> event.foodType = TACO.name
                        else -> event.foodType = NONE.name
                    }
                } else {
                    event.foodType = NONE.name
                }
                saveEventInDatastore(event)
                removeEvent()
            }

            removeEventButton.setOnClickListener {
                val eventToSave = Event()
                eventToSave.id = event.id
                eventToSave.isFood = false
                saveEventInDatastore(eventToSave)
                removeEvent()
            }
        }

        private fun saveEventInDatastore(eventToSave: Event) {
            eventsRepository.saveEventRX(eventToSave)
                    .subscribe()
        }

        fun bindEvent(event: Event) {
            this.event = event
            titleTextView.text = event.name
            groupTextView.text = event.group.name
            eventLinkTextView.text = event.event_url
        }

        private fun removeEvent() {
            events = events.minus(event)
            notifyItemRemoved(position)
        }
    }
}