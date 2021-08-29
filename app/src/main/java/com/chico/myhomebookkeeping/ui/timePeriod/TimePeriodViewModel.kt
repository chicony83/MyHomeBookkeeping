package com.chico.myhomebookkeeping.ui.timePeriod

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.GetSP
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.helpers.SetSP
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate

class TimePeriodViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val argsStartTimePeriod = Constants.FOR_QUERY_START_TIME_PERIOD
    private val argsEndTimePeriod = Constants.FOR_QUERY_END_TIME_PERIOD

    private val sharedPreferences = app.getSharedPreferences(spName, MODE_PRIVATE)
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(sharedPreferences.edit())
    private val modelCheck = ModelCheck()

    private var startTimePeriodLong: Long = minusOneLong
    private var endTimePeriodLong: Long = minusOneLong

    private val _startTimePeriodText = MutableLiveData<String>()
    val startTimePeriodText: LiveData<String>
        get() = _startTimePeriodText

    private val _endTimePeriodText = MutableLiveData<String>()
    val endTimePeriodText: LiveData<String>
        get() = _endTimePeriodText

    init {
        getARGSFromSP()
    }

    private fun getARGSFromSP() {
        startTimePeriodLong = getSP.getLong(argsStartTimePeriod)
        endTimePeriodLong = getSP.getLong(argsEndTimePeriod)
        if (modelCheck.isPositiveValue(startTimePeriodLong)) {
            postStartTimePeriod()
        }
        if (modelCheck.isPositiveValue(endTimePeriodLong)) {
            postEndTimePeriod()
        }
    }

    fun setStartTimePeriod(date: Long) {
        messageLog("start time = $date")
        startTimePeriodLong = date
        postStartTimePeriod()
    }

    private fun postStartTimePeriod() {
        if (modelCheck.isPositiveValue(startTimePeriodLong)) {
            _startTimePeriodText.postValue(startTimePeriodLong.parseTimeFromMillisShortDate())
        } else {
            _startTimePeriodText.postValue(getStringResource(R.string.time_period_at_first_text_on_button))
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
            _endTimePeriodText.postValue(getStringResource(R.string.time_period_to_end_text_on_button))
        }

    }

    fun getStartTimePeriod(): Long {
        return startTimePeriodLong
    }
    fun getEndTimePeriod():Long{
        return endTimePeriodLong
    }

    fun saveARGStoSP() {
        setSP.setLong(argsStartTimePeriod, startTimePeriodLong)
        setSP.setLong(argsEndTimePeriod, endTimePeriodLong)
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
}
