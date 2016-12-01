package com.austindroids.austinfeedsme.eventsmap;

import android.content.Intent;
import android.net.Uri;
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
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {
    private List<CardView> mViews;
    private List<Event> events;
    private float mBaseElevation;

    public CardPagerAdapter(List<Event> events) {

        this.events = events;
        mViews = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
//            events.add(new Event());
            mViews.add(null);
        }
    }
    public float getBaseElevation() {
        return mBaseElevation;
    }
    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
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
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        TextView titleText = (TextView) view.findViewById(R.id.card_title_text);
        TextView bodyText = (TextView) view.findViewById(R.id.card_body_text);
        Button rsvpButton= (Button) view.findViewById(R.id.card_rsvp_button);
        rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rsvpLink = events.get(position).getEvent_url();
                if (rsvpLink != null)
                {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(rsvpLink));
                    view.getContext().startActivity(i);
                }
            }
        });

        titleText.setText(events.get(position).getName());
        bodyText.setText(events.get(position).getDescription());
        bodyText.setMovementMethod(new ScrollingMovementMethod());

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    public Event getEventAtPosition(int position) {
        return events.get(position);
    }

}
