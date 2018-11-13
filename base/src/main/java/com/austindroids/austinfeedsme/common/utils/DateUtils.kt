package com.austindroids.austinfeedsme.common.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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


        return if (timestamp != null) {
            sdf.format(Date(timestamp))
        } else {
            sdf.format(Date().time)
        }
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

    fun aMinuteFromMinuteToday() : Long {
        val currentDay = Calendar.getInstance()
        currentDay.set(Calendar.HOUR_OF_DAY, 23)
        currentDay.set(Calendar.MINUTE, 59)
        return currentDay.timeInMillis
    }

    fun sevenDaysFromNow() : Long {
        val sevenDaysFromNowCalendar = Calendar.getInstance()
        sevenDaysFromNowCalendar.add(Calendar.DATE, +7)
        return sevenDaysFromNowCalendar.timeInMillis
    }

}
