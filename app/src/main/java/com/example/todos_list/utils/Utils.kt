package com.example.to_do_list.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**************************************
 * FUNCTIONS
 *************************************/

fun getTodayDate(pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Calendar.getInstance().time)
}

fun getTimeDifference(
    format: SimpleDateFormat,
    endingDate: String,
    startingDate: String,
    timeUnit: TimeUnit
): Long {
    return try {
        timeUnit.convert(
            (format.parse(endingDate)?.time ?: 0) - (format.parse(startingDate)?.time ?: 0),
            TimeUnit.MILLISECONDS
        )
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}