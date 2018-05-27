package com.austindroids.austinfeedsme.utility

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by daz on 8/7/16.
 */
object DateUtils {

    fun getLocalDateFromTimestamp(timestamp: Long?): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone

        /* date formatter in local timezone */
        val sdf = SimpleDateFormat("EEEE, MMMM d h:mm a", Locale.US)
        sdf.timeZone = tz

        return sdf.format(Date(timestamp!!))
    }

    fun getUnixTimeFromISO8601(iso8601Date: String): Long? {
        val dateParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        dateParser.timeZone = TimeZone.getTimeZone("UTC")
        var dateInMilli: Long? = null
        try {
            dateInMilli = dateParser.parse(iso8601Date).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateInMilli
    }

}
