package com.austindroids.austinfeedsme.eventsmap;

import android.support.v7.widget.CardView;

/**
 * Created by pauljoiner on 9/11/16.
 */
public interface CardAdapter {
    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
