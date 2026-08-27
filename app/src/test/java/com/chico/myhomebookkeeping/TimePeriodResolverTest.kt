package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.timePeriod.TimePeriodResolver
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class TimePeriodResolverTest {

    private val timeZone: TimeZone = TimeZone.getTimeZone("Europe/Warsaw")

    @Test
    fun last30DaysUsesTodayEndAndStartAtLocalMidnight() {
        val now = calendar(2026, Calendar.AUGUST, 27, 15, 30, 0, 123)

        val result = TimePeriodResolver.resolve(
            Constants.TIME_PERIOD_MODE_LAST_30_DAYS,
            Constants.MINUS_ONE_VAL_LONG,
            Constants.MINUS_ONE_VAL_LONG,
            now
        )

        assertEquals(calendar(2026, Calendar.JULY, 29, 0, 0, 0, 0).timeInMillis, result.startTime)
        assertEquals(calendar(2026, Calendar.AUGUST, 27, 23, 59, 59, 999).timeInMillis, result.endTime)
    }

    @Test
    fun lastMonthUsesFullPreviousMonth() {
        val now = calendar(2026, Calendar.AUGUST, 27, 15, 30, 0, 123)

        val result = TimePeriodResolver.resolve(
            Constants.TIME_PERIOD_MODE_LAST_MONTH,
            Constants.MINUS_ONE_VAL_LONG,
            Constants.MINUS_ONE_VAL_LONG,
            now
        )

        assertEquals(calendar(2026, Calendar.JULY, 1, 0, 0, 0, 0).timeInMillis, result.startTime)
        assertEquals(calendar(2026, Calendar.JULY, 31, 23, 59, 59, 999).timeInMillis, result.endTime)
    }

    @Test
    fun customModeKeepsSavedTimes() {
        val start = calendar(2026, Calendar.JULY, 1, 0, 0, 0, 0).timeInMillis
        val end = calendar(2026, Calendar.AUGUST, 31, 23, 59, 59, 999).timeInMillis

        val result = TimePeriodResolver.resolve(
            Constants.TIME_PERIOD_MODE_CUSTOM,
            start,
            end,
            calendar(2026, Calendar.SEPTEMBER, 10, 12, 0, 0, 0)
        )

        assertEquals(start, result.startTime)
        assertEquals(end, result.endTime)
    }

    private fun calendar(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        millisecond: Int
    ): Calendar {
        return Calendar.getInstance(timeZone).apply {
            clear()
            set(year, month, day, hour, minute, second)
            set(Calendar.MILLISECOND, millisecond)
        }
    }
}
