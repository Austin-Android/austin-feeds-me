package com.austindroids.austinfeedsme.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by darrankelinske on 3/1/17.
 */

public class NetworkUtils {

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
