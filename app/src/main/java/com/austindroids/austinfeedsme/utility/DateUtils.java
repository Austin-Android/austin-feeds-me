package com.austindroids.austinfeedsme.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by daz on 8/7/16.
 */
public class DateUtils {

    public static String getLocalDateFromTimestamp (Long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMM d KK:mm a");
        sdf.setTimeZone(tz);

        String localTime = sdf.format(new Date(timestamp));
        return localTime;
    }

}
