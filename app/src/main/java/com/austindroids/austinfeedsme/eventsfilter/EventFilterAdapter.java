package com.austindroids.austinfeedsme.eventsfilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.austindroids.austinfeedsme.data.Event.Type.BEER;
import static com.austindroids.austinfeedsme.data.Event.Type.NONE;
import static com.austindroids.austinfeedsme.data.Event.Type.PIZZA;
import static com.austindroids.austinfeedsme.data.Event.Type.TACO;

/**
 * Created by darrankelinske on 8/26/16.
 */
public class EventFilterAdapter extends RecyclerView.Adapter<EventFilterAdapter.ViewHolder> {

    private List<Event> events;

    public EventFilterAdapter(List<Event> events) {
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

    public void addEvent(Event event) {
        events.add(event);
        notifyDataSetChanged();
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

            author = (TextView) itemView.findViewById(R.id.event_title);
            group = (TextView) itemView.findViewById(R.id.event_group_name);
            quote = (TextView) itemView.findViewById(R.id.event_text);
            link = (TextView) itemView.findViewById(R.id.event_link);
            addEvent = (Button) itemView.findViewById(R.id.add_event);
            removeEvent = (Button) itemView.findViewById(R.id.remove_event);

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
                    }else{
                        event.setFoodType(NONE.name());
                    }

                    String eventName = event.getName();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference eventsReference = database.getReference("events");

                    eventsReference.push().setValue(event);
                    Toast.makeText(v.getContext(), eventName + " added!",
                            Toast.LENGTH_SHORT).show();

                    removeAt(getAdapterPosition());

                }
            });

            removeEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = getItem(getAdapterPosition());
                    event.setFood(false);

                    String eventName = event.getName();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("events");

                    myRef.push().setValue(event);

                    Toast.makeText(v.getContext(), eventName + " removed!",
                            Toast.LENGTH_SHORT).show();

                    removeAt(getAdapterPosition());

                }
            });

        }

        public void removeAt(int position) {
            events.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, events.size());
        }

    }
}