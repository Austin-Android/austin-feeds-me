package com.austindroids.austinfeedsme.eventsfilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.EventsRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.austindroids.austinfeedsme.data.Event.Type.BEER;
import static com.austindroids.austinfeedsme.data.Event.Type.NONE;
import static com.austindroids.austinfeedsme.data.Event.Type.PIZZA;
import static com.austindroids.austinfeedsme.data.Event.Type.TACO;


public class EventFilterAdapter extends RecyclerView.Adapter<EventFilterAdapter.ViewHolder> {

    private final EventsRepository eventsRepository;
    private List<Event> events;

    public EventFilterAdapter(List<Event> events, EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
        setList(events);
    }

    @Override
    public EventFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View quoteView = inflater.inflate(R.layout.choose_meetup_item, parent, false);

        return new EventFilterAdapter.ViewHolder(quoteView);
    }

    @Override
    public void onBindViewHolder(EventFilterAdapter.ViewHolder viewHolder, int position) {
        Event event = events.get(position);

        viewHolder.author.setText(event.getName());
        viewHolder.group.setText(event.getGroup().getName());

        if (null != event.getDescription()) {

            String description = event.getDescription().replaceAll("pizza", "<font color='red'>" + "PIZZA" + "</font>");
            description = description.replaceAll("Pizza", "<font color='red'>" + "PIZZA" + "</font>");
            description = description.replaceAll("provide", "<font color='red'>" + "PROVIDE" + "</font>");
            description = description.replaceAll("provided", "<font color='red'>" + "PROVIDED" + "</font>");
            description = description.replaceAll("taco", "<font color='red'>" + "taco" + "</font>");
            description = description.replaceAll("beer", "<font color='red'>" + "beer" + "</font>");
            description = description.replaceAll("drinks", "<font color='red'>" + "drinks" + "</font>");

            viewHolder.quote.setText(Html.fromHtml(description));
        }
        viewHolder.link.setText(event.getEvent_url());
    }

    private void setList(List<Event> events) {
        this.events = events;
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public void replaceData(List<Event> quotes){
        setList(quotes);
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

        private TextView author;
        private TextView group;
        private TextView quote;
        private TextView link;
        private Button addEvent;
        private Button removeEvent;

        public ViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.event_title);
            group = itemView.findViewById(R.id.event_group_name);
            quote = itemView.findViewById(R.id.event_text);
            link = itemView.findViewById(R.id.event_link);
            addEvent = itemView.findViewById(R.id.add_event);
            removeEvent = itemView.findViewById(R.id.remove_event);

            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = getItem(getAdapterPosition());
                    event.setFood(true);

                    if (event.getDescription().toUpperCase().contains(PIZZA.name())) {
                        event.setFoodType(PIZZA.name());
                    } else if (event.getDescription().toUpperCase().contains(BEER.name())) {
                        event.setFoodType(BEER.name());
                    } else if (event.getDescription().toUpperCase().contains(TACO.name())) {
                        event.setFoodType(TACO.name());
                    } else {
                        event.setFoodType(NONE.name());
                    }

                    eventsRepository.saveEvent(event, null);

                    removeAt(getAdapterPosition());
                }
            });

            removeEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = getItem(getAdapterPosition());

                    Event eventToSave = new Event();
                    eventToSave.setId(event.getId());
                    eventToSave.setFood(false);

                    eventsRepository.saveEvent(eventToSave, null);

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