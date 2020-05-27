package com.gopal.kotlinmessenger.utils

import android.text.format.DateUtils
import android.text.format.DateUtils.isToday
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.Date

object `date-time` {

    val formatted_time = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())
    val Time_only = SimpleDateFormat("h: mm a", Locale.getDefault())
    val Date_only = SimpleDateFormat("d MMM", Locale.getDefault())

    fun getFormattedTime(timeInMillis: Long): String{
        val date = Date(timeInMillis * 1000L)

        return when {
            isToday(date) -> Time_only.format(date)
            isYesterday(date) -> "Yesterday"
            else -> Date_only.format(date)
        }
    }

    fun getFormattedTimeChatLog(timeInMilis: Long): String {
        val date = Date(timeInMilis * 1000L)
        val formatted_time = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())
        val Time_only = SimpleDateFormat("h:mm a", Locale.getDefault())

        return when {
            isToday(date) -> Time_only.format(date)
            else -> formatted_time.format(date)
        }

    }


    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

}