package com.example.to_do_list.utils

import java.text.SimpleDateFormat
import java.util.*

fun calendarToString(calendar: Calendar, pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(calendar.time)
}