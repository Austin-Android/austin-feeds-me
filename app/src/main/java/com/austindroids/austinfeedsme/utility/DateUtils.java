package com.austindroids.austinfeedsme.utility;

import java.text.DateFormat;
import java.text.ParseException;
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d h:mm a");
        sdf.setTimeZone(tz);

        String localTime = sdf.format(new Date(timestamp));
        return localTime;
    }

    public static Long getUnixTimeFromISO8601(String iso8601Date) {
        DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        Long dateInMilli = null;
        try {
            dateInMilli = dateParser.parse(iso8601Date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateInMilli;
    }

}
