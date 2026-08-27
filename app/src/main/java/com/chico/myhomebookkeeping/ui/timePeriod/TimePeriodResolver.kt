package com.chico.myhomebookkeeping.ui.timePeriod

import com.chico.myhomebookkeeping.obj.Constants
import java.util.Calendar
import java.util.TimeZone

data class TimePeriodRange(
    val startTime: Long,
    val endTime: Long
)

object TimePeriodResolver {

    fun resolve(
        mode: String?,
        customStartTime: Long,
        customEndTime: Long,
        now: Calendar = Calendar.getInstance()
    ): TimePeriodRange {
        val safeMode = mode.takeUnless { it.isNullOrBlank() } ?: Constants.TIME_PERIOD_MODE_CUSTOM
        return when (safeMode) {
            Constants.TIME_PERIOD_MODE_ALL_TIME -> TimePeriodRange(
                Constants.MINUS_ONE_VAL_LONG,
                Constants.MINUS_ONE_VAL_LONG
            )
            Constants.TIME_PERIOD_MODE_THIS_WEEK -> thisWeek(now)
            Constants.TIME_PERIOD_MODE_LAST_WEEK -> lastWeek(now)
            Constants.TIME_PERIOD_MODE_THIS_MONTH -> thisMonth(now)
            Constants.TIME_PERIOD_MODE_LAST_MONTH -> lastMonth(now)
            Constants.TIME_PERIOD_MODE_LAST_28_DAYS -> lastDays(28, now)
            Constants.TIME_PERIOD_MODE_LAST_30_DAYS -> lastDays(30, now)
            Constants.TIME_PERIOD_MODE_LAST_90_DAYS -> lastDays(90, now)
            Constants.TIME_PERIOD_MODE_LAST_180_DAYS -> lastDays(180, now)
            Constants.TIME_PERIOD_MODE_LAST_365_DAYS -> lastDays(365, now)
            else -> TimePeriodRange(customStartTime, customEndTime)
        }
    }

    fun utcDateSelectionToLocalDayStart(selection: Long): Long {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = selection
        }
        return Calendar.getInstance().apply {
            clear()
            set(
                utcCalendar.get(Calendar.YEAR),
                utcCalendar.get(Calendar.MONTH),
                utcCalendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun utcDateSelectionToLocalDayEnd(selection: Long): Long {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = selection
        }
        return Calendar.getInstance().apply {
            clear()
            set(
                utcCalendar.get(Calendar.YEAR),
                utcCalendar.get(Calendar.MONTH),
                utcCalendar.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59
            )
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun localMillisToUtcDateSelection(time: Long): Long {
        val localCalendar = Calendar.getInstance().apply {
            timeInMillis = time
        }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                localCalendar.get(Calendar.YEAR),
                localCalendar.get(Calendar.MONTH),
                localCalendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun thisWeek(now: Calendar): TimePeriodRange {
        val start = dayStart(now).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val end = dayEnd(now)
        return TimePeriodRange(start.timeInMillis, end.timeInMillis)
    }

    private fun lastWeek(now: Calendar): TimePeriodRange {
        val start = dayStart(now).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 6)
            setToEndOfDay()
        }
        return TimePeriodRange(start.timeInMillis, end.timeInMillis)
    }

    private fun thisMonth(now: Calendar): TimePeriodRange {
        val start = dayStart(now).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val end = dayEnd(now)
        return TimePeriodRange(start.timeInMillis, end.timeInMillis)
    }

    private fun lastMonth(now: Calendar): TimePeriodRange {
        val start = dayStart(now).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -1)
        }
        val end = (start.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            setToEndOfDay()
        }
        return TimePeriodRange(start.timeInMillis, end.timeInMillis)
    }

    private fun lastDays(days: Int, now: Calendar): TimePeriodRange {
        val start = dayStart(now).apply {
            add(Calendar.DAY_OF_YEAR, -(days - 1))
        }
        val end = dayEnd(now)
        return TimePeriodRange(start.timeInMillis, end.timeInMillis)
    }

    private fun dayStart(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun dayEnd(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            setToEndOfDay()
        }
    }

    private fun Calendar.setToEndOfDay() {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}
