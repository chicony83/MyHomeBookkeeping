package com.chico.myhomebookkeeping.sp

import android.content.SharedPreferences
import android.util.Log
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.helpers.NavControlHelper

class SetSP(private val spEditor: SharedPreferences.Editor) {
    private val navNewMoneyMoving = R.id.nav_new_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query
    private val navMoneyMoving = R.id.nav_money_moving
    private val navChangeMoneyMoving = R.id.nav_change_money_moving

    private val navReportsMenu = R.id.nav_reports_menu

    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH
    private val argsStartTimePeriodForQuery = Constants.FOR_QUERY_START_TIME_PERIOD
    private val argsEndTimePeriodForQuery = Constants.FOR_QUERY_END_TIME_PERIOD
    private val argsStartTimePeriodForReport = Constants.FOR_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodForReports = Constants.FOR_REPORTS_END_TIME_PERIOD


    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val minusOneInt = Constants.MINUS_ONE_VAL_INT

    fun checkAndSaveToSP(
        navControlHelper: NavControlHelper,
        argsForNew: String,
        argsForChange: String,
        argsForQuery: String,
        id: Int?
    ) {
        when (navControlHelper.previousFragment()) {
            navMoneyMoving -> {
                saveToSP(argsForQuery, id)
            }
            navMoneyMovingQuery -> {
                saveToSP(argsForQuery, id)
            }
            navNewMoneyMoving -> {
                saveToSP(argsForNew, id)
            }
            navChangeMoneyMoving -> {
                saveToSP(argsForChange, id)
            }
        }
    }

    fun saveToSP(
        args: String,
        value: Int?
    ) {
        messageLog(args, value.toString())
        spEditor.putInt(args, value ?: minusOneInt)
        spCommit()
    }

//    fun saveToSp(
//        argsKey: String,
//        value: Boolean
//    ) {
//        messageLog(argsKey,value.toString())
//        spEditor.putBoolean(argsKey,value?:false)
//        spCommit()
//    }

    fun saveToSP(
        args: String,
        value: String?
    ) {
        messageLog(args, value.toString())
        spEditor.putString(args, value ?: "")
        spCommit()
    }

    fun saveToSP(
        args: String,
        value: Long?
    ) {
        messageLog(args, value.toString())
        spEditor.putLong(args, value ?: minusOneLong)
        spCommit()
    }

    fun saveIsIncomeCategoryToSP(argsIncomeSpending: String, value: String) {
        spEditor.putString(argsIncomeSpending, value)
        spCommit()
        messageLog(value)
    }

    private fun messageLog(argsKey: String, value: String) {

        Log.i("TAG", "--- save to SP $argsKey $value---")
    }

    private fun messageLog(value: String) {
        Log.i("TAG", "---$value---")
    }

    private fun spCommit() {
        spEditor.commit()
    }

    fun setLong(args: String, id: Long?) {
        spEditor.putLong(args, id ?: minusOneLong)
        spCommit()
    }

    fun saveToSP(args: String, float: Float) {
        spEditor.putFloat(args, float)
        messageLog(argsKey = args, float.toString())
        spCommit()
    }

    fun saveToSP(args: String, b: Boolean) {
        messageLog(argsKey = args, value = b.toString())
        spEditor.putBoolean(args, b)
        spCommit()
    }

    fun setIsFirstLaunchTrue() {
        spEditor.putBoolean(argsIsFirstLaunch, true)
        spCommit()
    }

    fun setIsFirstLaunchFalse() {
        spEditor.putBoolean(argsIsFirstLaunch, false)
        spCommit()
    }

    fun checkAndSaveToSpTimePeriod(
        navControlHelper: NavControlHelper,
        startTimePeriodLong: Long,
        endTimePeriodLong: Long
    ) {
        when(navControlHelper.previousFragment()){
            navMoneyMoving ->{
                saveToSP(argsStartTimePeriodForQuery,startTimePeriodLong)
                saveToSP(argsEndTimePeriodForQuery,endTimePeriodLong)
            }
            navReportsMenu ->{
                saveToSP(argsStartTimePeriodForReport,startTimePeriodLong)
                saveToSP(argsEndTimePeriodForReports,endTimePeriodLong)
            }
        }
    }
}