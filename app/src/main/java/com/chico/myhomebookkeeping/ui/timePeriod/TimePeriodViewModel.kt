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
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate

class TimePeriodViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val argsStartTimePeriodForQuery = Constants.ARGS_QUERY_PAYMENT_START_TIME_PERIOD
    private val argsEndTimePeriodForQuery = Constants.ARGS_QUERY_PAYMENT_END_TIME_PERIOD
    private val argsTimePeriodModeForQuery = Constants.ARGS_QUERY_PAYMENT_TIME_PERIOD_MODE
    private val argsStartTimePeriodForReport = Constants.ARGS_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodForReports = Constants.ARGS_REPORTS_END_TIME_PERIOD
    private val argsTimePeriodModeForReports = Constants.ARGS_REPORTS_TIME_PERIOD_MODE

    private val sharedPreferences = app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)
    private val modelCheck = ModelCheck()

    private var startTimePeriodLong: Long = minusOneLong
    private var endTimePeriodLong: Long = minusOneLong
    private var timePeriodMode: String = Constants.TIME_PERIOD_MODE_CUSTOM
    private val setText = SetTextOnButtons(app.resources)

    private val _startTimePeriodText = MutableLiveData<String>()
    val startTimePeriodText: LiveData<String>
        get() = _startTimePeriodText

    private val _endTimePeriodText = MutableLiveData<String>()
    val endTimePeriodText: LiveData<String>
        get() = _endTimePeriodText

    private val _selectedModeText = MutableLiveData<String>()
    val selectedModeText: LiveData<String>
        get() = _selectedModeText

    fun setCustomTimePeriod(startTime: Long, endTime: Long) {
        messageLog("custom time = $startTime - $endTime")
        startTimePeriodLong = startTime
        endTimePeriodLong = endTime
        timePeriodMode = Constants.TIME_PERIOD_MODE_CUSTOM
        postTimePeriod()
    }

    private fun messageLog(message: String) {
        Log.i("TAG", message)
    }

    fun setTimePeriodMode(mode: String) {
        timePeriodMode = mode
        postTimePeriod()
    }

    private fun postTimePeriod() {
        val resolvedPeriod = getResolvedTimePeriod()
        if (modelCheck.isPositiveValue(resolvedPeriod.startTime)) {
            _startTimePeriodText.postValue(resolvedPeriod.startTime.parseTimeFromMillisShortDate())
        } else {
            _startTimePeriodText.postValue(getStringResource(R.string.text_on_button_time_period_at_first))
        }

        if (modelCheck.isPositiveValue(resolvedPeriod.endTime)) {
            _endTimePeriodText.postValue(resolvedPeriod.endTime.parseTimeFromMillisShortDate())
        } else {
            _endTimePeriodText.postValue(getStringResource(R.string.text_on_button_time_period_to_end))
        }

        _selectedModeText.postValue(getModeText())
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
            endTimePeriodLong = endTimePeriodLong,
            timePeriodMode = timePeriodMode

        )
    }

    fun resetStartPeriod() {
        startTimePeriodLong = minusOneLong
        timePeriodMode = Constants.TIME_PERIOD_MODE_CUSTOM
        postTimePeriod()
    }

    fun resetEndPeriod() {
        endTimePeriodLong = minusOneLong
        timePeriodMode = Constants.TIME_PERIOD_MODE_CUSTOM
        postTimePeriod()
    }

    private fun getStringResource(text: Int): String {
        return app.getString(text)
    }

    fun setTextOnButtons(navControlHelper: NavControlHelper) {
        val argsStartTimePeriod:String
        val argsEndTimePeriod:String
        val argsTimePeriodMode:String
        when (navControlHelper.previousFragment()) {
            R.id.nav_money_moving -> {
                argsStartTimePeriod = argsStartTimePeriodForQuery
                argsEndTimePeriod = argsEndTimePeriodForQuery
                argsTimePeriodMode = argsTimePeriodModeForQuery
            }
            else -> {
                argsStartTimePeriod = argsStartTimePeriodForReport
                argsEndTimePeriod = argsEndTimePeriodForReports
                argsTimePeriodMode = argsTimePeriodModeForReports
            }
        }
        getARGSFromSP(argsStartTimePeriod,argsEndTimePeriod,argsTimePeriodMode)
    }

    private fun getARGSFromSP(
        argsStartTimePeriod: String,
        argsEndTimePeriod: String,
        argsTimePeriodMode: String
    ) {
        startTimePeriodLong = getSP.getLong(argsStartTimePeriod)
        endTimePeriodLong = getSP.getLong(argsEndTimePeriod)
        timePeriodMode = getSP.getString(argsTimePeriodMode).takeUnless { it.isNullOrBlank() }
            ?: Constants.TIME_PERIOD_MODE_CUSTOM
        postTimePeriod()
    }

    fun getTimePeriodMode(): String {
        return timePeriodMode
    }

    fun getResolvedTimePeriod(): TimePeriodRange {
        return TimePeriodResolver.resolve(timePeriodMode, startTimePeriodLong, endTimePeriodLong)
    }

    private fun getModeText(): String {
        return setText.timePeriodModeText(timePeriodMode).ifEmpty {
            getStringResource(R.string.text_on_button_time_period_custom)
        }
    }

}
