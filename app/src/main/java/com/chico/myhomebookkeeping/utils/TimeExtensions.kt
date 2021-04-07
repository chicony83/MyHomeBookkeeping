package com.chico.myhomebookkeeping.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.parseTimeToMillis(pattern: String = "yyyy-MM-dd HH:mm"):Long{
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.parse(this).time
}

fun Long.parseTimeFromMillis(pattern: String = "yyyy-MM-dd HH:mm"):String{
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}
