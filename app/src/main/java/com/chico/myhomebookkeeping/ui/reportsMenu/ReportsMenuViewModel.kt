package com.chico.myhomebookkeeping.ui.reportsMenu

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate

class ReportsMenuViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val space = " "
    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)
    private val getSP = GetSP(sharedPreferences)

    private val argsReportType = Constants.REPORT_TYPE
    private val argsStartTimePeriod = Constants.ARGS_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriod = Constants.ARGS_REPORTS_END_TIME_PERIOD

    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var startTimePeriodLongSP = minusOneLong
    private var endTimePeriodLongSP = minusOneLong

    private val _buttonTextOfTimePeriod = MutableLiveData<String>()
    val buttonTextOfTimePeriod: LiveData<String>
        get() = _buttonTextOfTimePeriod

    private val modelCheck = ModelCheck()

    init {
        getValuesSP()
        setTextOnButtons()
    }

    private fun setTextOnButtons() {
        setTextOnTimePeriodButton()
    }

    private fun setTextOnTimePeriodButton() {
        launchUi {
            val text: String = getResourceText(R.string.text_on_button_time_period)
            var timePeriod = ""
            val textFrom = getResourceText(R.string.text_on_button_time_period_from)
            val textTo = getResourceText(R.string.text_on_button_time_period_to)
            val textAllTime = getResourceText(R.string.text_on_button_time_period_all_time)
            if (modelCheck.isPositiveValue(startTimePeriodLongSP)) {
                timePeriod =
                    textFrom +
                            space +
                            startTimePeriodLongSP.parseTimeFromMillisShortDate() +
                            space
            }
            if (modelCheck.isPositiveValue(endTimePeriodLongSP)) {
                timePeriod =
                    timePeriod +
                            space +
                            textTo +
                            space +
                            endTimePeriodLongSP.parseTimeFromMillisShortDate()
            }
            if ((!modelCheck.isPositiveValue(startTimePeriodLongSP))
                and (!modelCheck.isPositiveValue(endTimePeriodLongSP))
            ) {
                timePeriod = textAllTime
            }
            Message.log(timePeriod)
            _buttonTextOfTimePeriod.postValue(createButtonText(text, timePeriod))
        }
    }

    private fun getResourceText(string: Int): String {
        return app.getString(string)

    }

    private fun createButtonText(text: String, name: String): String {
        val separator: String = getNewLineSeparator()
        return text + separator + name
    }

    private fun getNewLineSeparator(): String {
        return "\n"
    }

    private fun getValuesSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriod)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriod)
    }

    fun saveArgs(report: String) {
//        Message.log("ARGS Saved, reports type = $report")
        setSP.saveToSP(argsReportType, report)
    }
}