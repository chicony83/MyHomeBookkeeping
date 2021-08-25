package com.chico.myhomebookkeeping.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun String.parseTimeToMillis(pattern: String = "yyyy-MM-dd HH:mm"):Long{
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.parse(this).time
}

@SuppressLint("SimpleDateFormat")
fun Long.parseTimeFromMillis(pattern: String = "yyyy-MM-dd HH:mm"):String{
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(this)

}fun Long.parseTimeFromMillisShortDate(pattern: String = "dd MMMM yyyy"):String{
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(this)
}