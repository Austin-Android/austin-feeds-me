package com.austindroids.austinfeedsme.eventsfilter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.austindroids.austinfeedsme.data.Event.Type.BEER;
import static com.austindroids.austinfeedsme.data.Event.Type.NONE;
import static com.austindroids.austinfeedsme.data.Event.Type.PIZZA;
import static com.austindroids.austinfeedsme.data.Event.Type.TACO;


public class EventFilterAdapter extends RecyclerView.Adapter<EventFilterAdapter.ViewHolder> {

    private final EventsRepository eventsRepository;
    private List<Event> events = new ArrayList<>();

    public EventFilterAdapter(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    @Override
    public EventFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View quoteView = inflater.inflate(R.layout.list_item_event_filter, parent, false);

        return new EventFilterAdapter.ViewHolder(quoteView);
    }

    @Override
    public void onBindViewHolder(EventFilterAdapter.ViewHolder viewHolder, int position) {
        Event event = events.get(position);

        viewHolder.titleTextView.setText(event.getName());
        viewHolder.groupTextView.setText(event.getGroup().getName());

        if (null != event.getDescription()) {

            String description = event.getDescription().replaceAll("pizza", "<font color='red'>" + "PIZZA" + "</font>");
            description = description.replaceAll("Pizza", "<font color='red'>" + "PIZZA" + "</font>");
            description = description.replaceAll("provide", "<font color='red'>" + "PROVIDE" + "</font>");
            description = description.replaceAll("provided", "<font color='red'>" + "PROVIDED" + "</font>");
            description = description.replaceAll("taco", "<font color='red'>" + "taco" + "</font>");
            description = description.replaceAll("beer", "<font color='red'>" + "beer" + "</font>");
            description = description.replaceAll("drinks", "<font color='red'>" + "drinks" + "</font>");

            viewHolder.eventDescriptionTextView.setText(Html.fromHtml(description));
        }

        viewHolder.eventLinkTextView.setText(event.getEvent_url());
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public Event getItem(int position) {
        return events.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView groupTextView;
        private TextView eventDescriptionTextView;
        private TextView eventLinkTextView;
        private Button addEventButton;
        private Button removeEventButton;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.event_title);
            groupTextView = itemView.findViewById(R.id.event_group_name);
            eventDescriptionTextView = itemView.findViewById(R.id.event_text);
            eventLinkTextView = itemView.findViewById(R.id.event_link);
            addEventButton = itemView.findViewById(R.id.button_add_event);
            removeEventButton = itemView.findViewById(R.id.button_remove_event);

            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = getItem(getAdapterPosition());
                    event.setFood(true);

                    if (event.getDescription() != null) {
                        if (event.getDescription().toUpperCase().contains(PIZZA.name())) {
                            event.setFoodType(PIZZA.name());
                        } else if (event.getDescription().toUpperCase().contains(BEER.name())) {
                            event.setFoodType(BEER.name());
                        } else if (event.getDescription().toUpperCase().contains(TACO.name())) {
                            event.setFoodType(TACO.name());
                        } else {
                            event.setFoodType(NONE.name());
                        }
                    } else {
                        event.setFoodType(NONE.name());
                    }

                    eventsRepository.saveEventRX(event)
                            .subscribe();

                    removeAt(getAdapterPosition());
                }
            });

            removeEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = getItem(getAdapterPosition());

                    Event eventToSave = new Event();
                    eventToSave.setId(event.getId());
                    eventToSave.setFood(false);

                    eventsRepository.saveEventRX(eventToSave)
                            .subscribe();

                    removeAt(getAdapterPosition());
                }
            });
        }

        public void removeAt(int position) {
            events.remove(position);
            notifyItemRemoved(position);
        }
    }
}