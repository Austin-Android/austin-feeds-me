package com.austindroids.austinfeedsme.choosemeetup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.data.Results;
import com.austindroids.austinfeedsme.data.meetup.MeetupService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends AppCompatActivity {

    private static final String TAG ="EventFilterActivity";

    private RecyclerView eventsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("events");
        myRef.keepSynced(true);

        cleanEvents();

        eventsRecyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MeetupService meetupService = retrofit.create(MeetupService.class);
        Call<Results> call =
                meetupService.getEventsWithPizza();

        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Results results = response.body();
                Log.d(TAG, "Event count from API: " + results.getEvents().size());
                final List<Event> eventsFromRest = results.getEvents();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Event event = postSnapshot.getValue(Event.class);


                            Iterator<Event> iter = eventsFromRest.iterator();

                            while (iter.hasNext()) {
                                Event nextEvent = iter.next();
                                if (event.getId() != null &&
                                        event.getId().equals(nextEvent.getId()))
                                    iter.remove();
                            }


                        }
                        Log.d(TAG, "After cleaning we have this many events: " + eventsFromRest.size());
                        if (eventsFromRest.size() == 0) {
                            Toast.makeText(EventFilterActivity.this,
                                    "All events for the next 3 months have been filtered!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        ChooseEventsAdapter adapter = new ChooseEventsAdapter(eventsFromRest);
                        eventsRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });




    }

    public void cleanEvents() {
        final List<Event> events = new ArrayList<Event>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("events");
        myRef.orderByChild("time");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if(event.getTime() < (new Date().getTime() - 2678400000L)) {
                        Log.i("clean", "onDataChange: " + snapshot.getRef());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }



    private static class ChooseEventsAdapter extends RecyclerView.Adapter<ChooseEventsAdapter.ViewHolder> {

        private List<Event> events;

        public ChooseEventsAdapter(List<Event> events) {
            setList(events);
        }

        @Override
        public ChooseEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View quoteView = inflater.inflate(R.layout.choose_meetup_item, parent, false);

            return new ChooseEventsAdapter.ViewHolder(quoteView);
        }

        @Override
        public void onBindViewHolder(ChooseEventsAdapter.ViewHolder viewHolder, int position) {
            Event event = events.get(position);

            viewHolder.author.setText(event.getName());
            viewHolder.group.setText(event.getGroup().getName());

            if (null != event.getDescription()) {

                String description = event.getDescription().replaceAll("pizza", "<font color='red'>" + "PIZZA" + "</font>");
                description = description.replaceAll("Pizza", "<font color='red'>" + "PIZZA" + "</font>");
                description = description.replaceAll("provide", "<font color='red'>" + "PROVIDE" + "</font>");
                description = description.replaceAll("provided", "<font color='red'>" + "PROVIDED" + "</font>");

                viewHolder.quote.setText(Html.fromHtml(description));
            }

            viewHolder.link.setText(event.getEvent_url());


        }

        private void setList(List<Event> quotes) {
            events = quotes;
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

                        String eventName = event.getName();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("events");

                        myRef.push().setValue(event);
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
}
