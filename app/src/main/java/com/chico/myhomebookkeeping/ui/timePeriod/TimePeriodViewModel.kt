package com.chico.myhomebookkeeping.ui.timePeriod

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate

class TimePeriodViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val argsStartTimePeriodForQuery = Constants.FOR_QUERY_START_TIME_PERIOD
    private val argsEndTimePeriodForQuery = Constants.FOR_QUERY_END_TIME_PERIOD
    private val argsStartTimePeriodForReport = Constants.FOR_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodForReports = Constants.FOR_REPORTS_END_TIME_PERIOD

    private val sharedPreferences = app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)
    private val modelCheck = ModelCheck()

    private var startTimePeriodLong: Long = minusOneLong
    private var endTimePeriodLong: Long = minusOneLong

    private val _startTimePeriodText = MutableLiveData<String>()
    val startTimePeriodText: LiveData<String>
        get() = _startTimePeriodText

    private val _endTimePeriodText = MutableLiveData<String>()
    val endTimePeriodText: LiveData<String>
        get() = _endTimePeriodText

    fun setStartTimePeriod(date: Long) {
        messageLog("start time = $date")
        startTimePeriodLong = date
        postStartTimePeriod()
    }

    private fun postStartTimePeriod() {
        if (modelCheck.isPositiveValue(startTimePeriodLong)) {
            _startTimePeriodText.postValue(startTimePeriodLong.parseTimeFromMillisShortDate())
        } else {
            _startTimePeriodText.postValue(getStringResource(R.string.text_on_button_time_period_at_first))
        }
    }

    private fun messageLog(message: String) {
        Log.i("TAG", message)
    }

    fun setEndTimePeriod(date: Long) {
        messageLog("end date = $date")
        endTimePeriodLong = date
        postEndTimePeriod()
    }

    private fun postEndTimePeriod() {
        if (modelCheck.isPositiveValue(endTimePeriodLong)) {
            _endTimePeriodText.postValue(endTimePeriodLong.parseTimeFromMillisShortDate())
        } else {
            _endTimePeriodText.postValue(getStringResource(R.string.text_on_button_time_period_to_end))
        }

    }

    fun getStartTimePeriod(): Long {
        return startTimePeriodLong
    }

    fun getEndTimePeriod(): Long {
        return endTimePeriodLong
    }

    fun saveARGStoSP(navControlHelper: NavControlHelper) {
        setSP.checkAndSaveToSpTimePeriod(
            navControlHelper = navControlHelper,
            startTimePeriodLong = startTimePeriodLong,
            endTimePeriodLong = endTimePeriodLong

        )
//        setSP.setLong(argsStartTimePeriodForQuery, startTimePeriodLong)
//        setSP.setLong(argsEndTimePeriodForQuery, endTimePeriodLong)
    }

    fun resetStartPeriod() {
        startTimePeriodLong = minusOneLong
        postStartTimePeriod()
    }

    fun resetEndPeriod() {
        endTimePeriodLong = minusOneLong
        postEndTimePeriod()
    }

    private fun getStringResource(text: Int): String {
        return app.getString(text)
    }

    fun setTextOnButtons(navControlHelper: NavControlHelper) {
        val argsStartTimePeriod:String
        val argsEndTimePeriod:String
        when (navControlHelper.previousFragment()) {
            R.id.nav_money_moving -> {
                argsStartTimePeriod = argsStartTimePeriodForQuery
                argsEndTimePeriod = argsEndTimePeriodForQuery
            }
            else -> {
                argsStartTimePeriod = argsStartTimePeriodForReport
                argsEndTimePeriod = argsEndTimePeriodForReports
            }
        }
        getARGSFromSP(argsStartTimePeriod,argsEndTimePeriod)
    }

    private fun getARGSFromSP(argsStartTimePeriod: String, argsEndTimePeriod: String) {
        startTimePeriodLong = getSP.getLong(argsStartTimePeriod)
        endTimePeriodLong = getSP.getLong(argsEndTimePeriod)
        if (modelCheck.isPositiveValue(startTimePeriodLong)) {
            postStartTimePeriod()
        }
        if (modelCheck.isPositiveValue(endTimePeriodLong)) {
            postEndTimePeriod()
        }
    }

}
