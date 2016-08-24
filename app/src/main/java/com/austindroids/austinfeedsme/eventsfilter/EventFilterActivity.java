package com.austindroids.austinfeedsme.eventsfilter;

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
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEvent;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteEvents;
import com.austindroids.austinfeedsme.data.eventbrite.EventbriteService;
import com.austindroids.austinfeedsme.data.meetup.MeetupService;
import com.austindroids.austinfeedsme.utility.TypeUtils;
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
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by darrankelinske on 8/4/16.
 */
public class EventFilterActivity extends AppCompatActivity {

    private static final String TAG ="EventFilterActivity";

    private RecyclerView eventsRecyclerView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);

        myRef.keepSynced(true);

        eventsRecyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ChooseEventsAdapter eventFilterAdapter = new ChooseEventsAdapter(new ArrayList<Event>());
        eventsRecyclerView.setAdapter(eventFilterAdapter);


        //cleanEvents();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MeetupService meetupService = retrofit.create(MeetupService.class);
        Call<Results> call =
                meetupService.getOpenEvents();

        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Results results = response.body();
                Log.d(TAG, "Event count from Meetup API: " + results.getEvents().size());

                final List<Event> meetupEventsFromTheFuture = results.getEvents();

                Iterator<Event> iterToGetEventsInFuture = meetupEventsFromTheFuture.iterator();

                while (iterToGetEventsInFuture.hasNext()) {
                    Event nextEvent = iterToGetEventsInFuture.next();
                    if (nextEvent.getTime() < new Date().getTime()) {
                        iterToGetEventsInFuture.remove();
                    }
                }
                Log.d(TAG, "Event count after cleaning past events: " +
                        meetupEventsFromTheFuture.size());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Event event = postSnapshot.getValue(Event.class);

                            Iterator<Event> iter = meetupEventsFromTheFuture.iterator();

                            while (iter.hasNext()) {
                                Event nextEvent = iter.next();
                                if (event.getId() != null &&
                                        event.getId().equals(nextEvent.getId())) {
                                    iter.remove();
                                }
                            }
                        }

                        Log.d(TAG, "After cleaning we have this many events: " +
                                meetupEventsFromTheFuture.size());
                        if (meetupEventsFromTheFuture.size() == 0) {
                            Toast.makeText(EventFilterActivity.this,
                                    "Loading events that need to be filtered",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                                eventFilterAdapter.addEvents(meetupEventsFromTheFuture);
                        }

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

        RxJavaCallAdapterFactory rxAdapter =
                RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());


        Retrofit eventbriteRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.eventbriteapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        EventbriteService eventbriteService = eventbriteRetrofit.create(EventbriteService.class);

        String[] searchList = new String[]{"taco","pizza", "beer", "breakfast", "lunch", "dinner"};

//        Observable<EventbriteEvents> eventbriteServiceTacoEvents =
//                eventbriteService.getEventsByKeyword("taco");
//        Observable<EventbriteEvents> eventbriteServicePizzaEvents =
//                eventbriteService.getEventsByKeyword("pizza");
//        Observable<EventbriteEvents> eventbriteServiceBeerEvents =
//                eventbriteService.getEventsByKeyword("beer");
//        Observable<EventbriteEvents> eventbriteServiceBreakfastEvents =
//                eventbriteService.getEventsByKeyword("breakfast");
//        Observable<EventbriteEvents> eventbriteServiceLunchEvents =
//                eventbriteService.getEventsByKeyword("lunch");
//        Observable<EventbriteEvents> eventbriteServiceDinnerEvents =
//                eventbriteService.getEventsByKeyword("dinner");



        Subscriber<EventbriteEvents> eventbriteEventsSubscriber = new Subscriber<EventbriteEvents>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                // cast to retrofit.HttpException to get the response code
                if (e instanceof HttpException) {
                    HttpException response = (HttpException)e;
                    int code = response.code();
                }

            }

            @Override
            public void onNext(EventbriteEvents eventbriteEvents) {
                cleanAndLoadEventbriteEvents(eventbriteEvents, eventFilterAdapter);
            }
        };

        for (String searchTerm : searchList) {
            Observable<EventbriteEvents> searchEventbriteEvents =
                    eventbriteService.getEventsByKeyword(searchTerm);
            Subscription searchSubscription = searchEventbriteEvents
                    .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(eventbriteEventsSubscriber);

            compositeSubscription.add(searchSubscription);

        }

//        Subscription tacoSubscription = eventbriteServiceTacoEvents
//                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(eventbriteEventsSubscriber);
//
//
//        compositeSubscription.add(tacoSubscription);

//        eventbriteServiceTacoEvents.defer()enqueue(eventbriteEventsCallback);
//        eventbriteServicePizzaEvents.enqueue(eventbriteEventsCallback);
//        eventbriteServiceBeerEvents.enqueue(eventbriteEventsCallback);
//        eventbriteServiceBreakfastEvents.enqueue(eventbriteEventsCallback);
//        eventbriteServiceLunchEvents.enqueue(eventbriteEventsCallback);
//        eventbriteServiceDinnerEvents.enqueue(eventbriteEventsCallback);

    }

    private void cleanAndLoadEventbriteEvents(EventbriteEvents eventbriteEvents,
                                              final ChooseEventsAdapter eventFilterAdapter) {
        final Long callbackTimestamp = new Date().getTime();
        Log.d(TAG, "onResponse: Event's from eventbrite " + callbackTimestamp + ":"
                +eventbriteEvents.getEvents().size());

        final List<Event> convertedEventbriteEvents = new ArrayList<Event>();
        for (EventbriteEvent eventbriteEvent : eventbriteEvents.getEvents()) {
            convertedEventbriteEvents.add(TypeUtils.transformEventBrite(eventbriteEvent));
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);

                    Iterator<Event> iter = convertedEventbriteEvents.iterator();

                    while (iter.hasNext()) {
                        Event nextEvent = iter.next();
                        if (event.getId() != null &&
                                event.getId().equals(nextEvent.getId())) {
                            iter.remove();
                        }

                    }
                }

                Log.d(TAG, "onResponse: Event's from after cleaning " + callbackTimestamp + ":"
                        +convertedEventbriteEvents.size());

                if (convertedEventbriteEvents.size() != 0) {
                    eventFilterAdapter.addEvents(convertedEventbriteEvents);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
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
                        Log.i(TAG, "this event could be cleaned from firebase" + snapshot.getRef());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

//    public List<Event> removeAlreadyFilteredEvents(final List<Event> events ) {
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("events");
//        myRef.keepSynced(true);
//
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Event event = postSnapshot.getValue(Event.class);
//
//                    Iterator<Event> iter = events.iterator();
//
//                    while (iter.hasNext()) {
//                        Event nextEvent = iter.next();
//                        if (event.getId() != null &&
//                                event.getId().equals(nextEvent.getId())) {
//                            iter.remove();
//                        }
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        return events;
//    }



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
