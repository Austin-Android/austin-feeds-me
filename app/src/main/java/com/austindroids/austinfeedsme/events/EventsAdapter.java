package com.austindroids.austinfeedsme.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;
import com.austindroids.austinfeedsme.utility.DateUtils;

import java.util.List;

/**
 * Created by darrankelinske on 12/3/16.
 */
class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<Event> mEvents;
    private EventsActivity.EventItemListener mItemListener;

    public EventsAdapter(Context context, List<Event> Events, EventsActivity.EventItemListener itemListener) {
        this.context = context;
        setList(Events);
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View EventView = inflater.inflate(R.layout.item_event, parent, false);

        return new ViewHolder(EventView, mItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Event event = mEvents.get(position);

        viewHolder.eventDate.setText(DateUtils.getLocalDateFromTimestamp(event.getTime()));
        viewHolder.title.setText(event.getName());

        Spanned result;
        Spanned rsvpLink;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(event.getDescription(), Html.FROM_HTML_MODE_LEGACY);
            rsvpLink = Html.fromHtml(
                    "<html><a href=\"" + event.getEventUrl() + "\">RSVP Here!</a></html>",
                    Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(event.getDescription());
            rsvpLink = Html.fromHtml(
                    "<html><a href=\"" + event.getEventUrl() + "\">RSVP Here!</a></html>");
        }
        viewHolder.description.setText(result);


        viewHolder.eventUrl.setMovementMethod(LinkMovementMethod.getInstance());
//            viewHolder.eventUrl.setText(rsvpLink);
        viewHolder.eventUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(event.getEventUrl()));
                context.startActivity(webIntent);
            }
        });
    }

    public void replaceData(List<Event> Events) {
        setList(Events);
        notifyDataSetChanged();
    }

    private void setList(List<Event> Events) {
        mEvents = Events;
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public Event getItem(int position) {
        return mEvents.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView eventDate;
        public TextView title;
        public TextView description;
        public Button eventUrl;
        private ImageView pizzaIcon;
        private ImageView beerIcon;
        private ImageView tacoIcon;
        private EventsActivity.EventItemListener mItemListener;

        public ViewHolder(View itemView, EventsActivity.EventItemListener listener) {
            super(itemView);
            mItemListener = listener;
            eventDate = (TextView) itemView.findViewById(R.id.event_detail_time);
            title = (TextView) itemView.findViewById(R.id.event_detail_title);
            description = (TextView) itemView.findViewById(R.id.event_detail_description);
            pizzaIcon = (ImageView) itemView.findViewById(R.id.event_pizza_icon);
            beerIcon = (ImageView) itemView.findViewById(R.id.event_beer_icon);
            tacoIcon = (ImageView) itemView.findViewById(R.id.event_taco_icon);
            eventUrl = (Button) itemView.findViewById(R.id.event_link);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Event Event = getItem(position);
            mItemListener.onEventClick(Event);

        }
    }

    public List<Event> getEvents() {
        return mEvents;
    }
}
