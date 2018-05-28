package com.austindroids.austinfeedsme.eventsmap;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.austindroids.austinfeedsme.R;
import com.austindroids.austinfeedsme.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pauljoiner on 9/11/16.
 */
public class CardPagerAdapter extends PagerAdapter {

    private static final int MAX_ELEVATION_FACTOR = 8;

    private List<Event> events;
    private float mBaseElevation;

    public CardPagerAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        CardView cardView = view.findViewById(R.id.cardView);
        TextView titleText = view.findViewById(R.id.card_title_text);
        TextView bodyText = view.findViewById(R.id.card_body_text);
        Button rsvpButton= view.findViewById(R.id.card_rsvp_button);
        rsvpButton.setOnClickListener(view1 -> {
            String rsvpLink = events.get(position).getEvent_url();
            if (rsvpLink != null)
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(rsvpLink));
                view1.getContext().startActivity(i);
            }
        });

        titleText.setText(events.get(position).getName());
        bodyText.setText(events.get(position).getDescription());
        bodyText.setMovementMethod(new ScrollingMovementMethod());

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public Event getEventAtPosition(int position) {
        return events.get(position);
    }
}
