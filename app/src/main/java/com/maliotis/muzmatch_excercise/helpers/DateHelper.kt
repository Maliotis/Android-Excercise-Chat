package com.maliotis.muzmatch_excercise.helpers

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by petrosmaliotis on 11/09/2020.
 */

object DateHelper {

    val TAG = DateHelper::class.java.simpleName

    public fun getStringFormattedDate(time: Long?): String {
        if (time != null) {
            val date = Date(time)
            // i.e Saturday November 2012 10:45
            val stringDate = SimpleDateFormat("EEEE MMMM yyyy HH:mm", Locale.UK).format(date)
            Log.d(TAG, "getStringFormattedDate: stringDate = $stringDate")
            return stringDate
        }
        return "Time was null :("
    }

    public fun getTimeDifferenceInHours(startTime: Long?, endTime: Long?): Long {
        var timeDiff = 0L
        if (startTime != null && endTime != null) {
            //  /1000 to get from millis to sec
            //  /60 to get from sec to min
            //  /60 to get from min to hour
            timeDiff = (endTime - startTime) / 1000 / 60 / 60
        }
        return timeDiff
    }

    public fun getTimeDifferenceInSeconds(startTime: Long?, endTime: Long?): Long {
        var timeDiff = 0L
        if (startTime != null && endTime != null) {
            timeDiff = (endTime - startTime) / 1000
        }
        return timeDiff
    }

}