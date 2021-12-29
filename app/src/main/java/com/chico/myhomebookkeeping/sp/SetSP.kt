package com.chico.myhomebookkeeping.sp

import android.content.SharedPreferences
import android.util.Log
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.helpers.NavControlHelper

class SetSP(private val spEditor: SharedPreferences.Editor) {
    private val navNewMoneyMoving = R.id.nav_new_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query
    private val navMoneyMoving = R.id.nav_money_moving
    private val navChangeMoneyMoving = R.id.nav_change_money_moving
    private val navNewFastPayment = R.id.nav_new_fast_money_moving_fragment
    private val navChangeFastPayment = R.id.nav_change_fast_payment_fragment

    private val navCategory = R.id.nav_categories

    private val navReportsMenu = R.id.nav_reports_menu
    private val navReports = R.id.nav_reports

    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH
    private val argsStartTimePeriodForQuery = Constants.ARGS_QUERY_PAYMENT_START_TIME_PERIOD
    private val argsEndTimePeriodForQuery = Constants.ARGS_QUERY_PAYMENT_END_TIME_PERIOD
    private val argsStartTimePeriodForReport = Constants.ARGS_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodForReports = Constants.ARGS_REPORTS_END_TIME_PERIOD

    private val argsCreateCategory = Constants.ARGS_NEW_PAYMENT_CATEGORY_KEY
    private val argsQueryCategory = Constants.ARGS_QUERY_PAYMENT_CATEGORY_KEY
    private val argsChangeCategory = Constants.ARGS_CHANGE_PAYMENT_CATEGORY_KEY
    private val argsNewFastPaymentCategory = Constants.ARGS_NEW_FAST_PAYMENT_CATEGORY
    private val argsChangeFastPaymentCategory = Constants.ARGS_CHANGE_FAST_PAYMENT_CATEGORY

    private val argsCreateCashAccount = Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY
    private val argsQueryCashAccount = Constants.ARGS_QUERY_PAYMENT_CASH_ACCOUNT_KEY
    private val argsChangeCashAccount = Constants.ARGS_CHANGE_PAYMENT_CASH_ACCOUNT_KEY
    private val argsNewFastPaymentCashAccount = Constants.ARGS_NEW_FAST_PAYMENT_CASH_ACCOUNT
    private val argsChangeFastPaymentCashAccount = Constants.ARGS_CHANGE_FAST_PAYMENT_CASH_ACCOUNT

    private val argsCreateCurrency = Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY
    private val argsQueryCurrency = Constants.ARGS_QUERY_PAYMENT_CURRENCY_KEY
    private val argsChangeCurrency = Constants.ARGS_CHANGE_PAYMENT_CURRENCY_KEY
    private val argsNewFastPaymentCurrency: String = Constants.ARGS_NEW_FAST_PAYMENT_CURRENCY
    private val argsChangeFastPaymentCurrency = Constants.ARGS_CHANGE_FAST_PAYMENT_CURRENCY

    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val minusOneInt = Constants.MINUS_ONE_VAL_INT


    fun checkAndSaveToSP(
        navControlHelper: NavControlHelper,
        id: Int
    ) {
        var args = ""
        when (navControlHelper.previousFragment()) {
            navMoneyMoving -> {
                when (navControlHelper.currentFragment()) {
                    R.id.nav_categories -> args = argsQueryCategory
                    R.id.nav_cash_account -> args = argsQueryCashAccount
                    R.id.nav_currencies -> args = argsQueryCurrency
                }
            }
            navNewMoneyMoving -> {
                when (navControlHelper.currentFragment()) {
                    R.id.nav_categories -> args = argsCreateCategory
                    R.id.nav_cash_account -> args = argsCreateCashAccount
                    R.id.nav_currencies -> args = argsCreateCurrency
                }
            }
            navChangeMoneyMoving -> {
                when (navControlHelper.currentFragment()) {
                    R.id.nav_categories -> args = argsChangeCategory
                    R.id.nav_cash_account -> args = argsChangeCashAccount
                    R.id.nav_currencies -> args = argsChangeCurrency
                }
            }
            navNewFastPayment -> {
                when (navControlHelper.currentFragment()) {
                    R.id.nav_categories -> args = argsNewFastPaymentCategory
                    R.id.nav_cash_account -> args = argsNewFastPaymentCashAccount
                    R.id.nav_currencies -> args = argsNewFastPaymentCurrency
                }
            }
            navChangeFastPayment -> {
                Message.log("previous fragment nav change Fast payment")
                when (navControlHelper.currentFragment()) {
                    R.id.nav_categories -> args = argsChangeFastPaymentCategory
                    R.id.nav_currencies -> args = argsChangeFastPaymentCurrency
                    R.id.nav_cash_account -> args = argsChangeFastPaymentCashAccount
                }
            }
        }
        saveToSP(args, id)
    }

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
            navNewFastPayment -> {

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
        when (navControlHelper.previousFragment()) {
            navMoneyMoving -> {
                saveToSP(argsStartTimePeriodForQuery, startTimePeriodLong)
                saveToSP(argsEndTimePeriodForQuery, endTimePeriodLong)
            }
            navReportsMenu -> {
                saveToSP(argsStartTimePeriodForReport, startTimePeriodLong)
                saveToSP(argsEndTimePeriodForReports, endTimePeriodLong)
            }
            navReports -> {
                saveToSP(argsStartTimePeriodForReport, startTimePeriodLong)
                saveToSP(argsEndTimePeriodForReports, endTimePeriodLong)
            }
        }
    }

    fun saveToSP(argsKey: String, setSelectedCategories: Set<String>) {
        spEditor.putStringSet(argsKey, setSelectedCategories)
        spCommit()
        Message.log("setSelectedCategories.size = ${setSelectedCategories.size}")
//        val transactionList: ArrayList<SurfaceControl.Transaction> = ArrayList()
    }
}