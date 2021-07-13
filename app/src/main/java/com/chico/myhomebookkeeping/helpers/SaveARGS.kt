package com.chico.myhomebookkeeping.helpers

import android.content.SharedPreferences
import android.util.Log
import com.chico.myhomebookkeeping.R

class SaveARGS(private val spEditor: SharedPreferences.Editor) {
    private val navNewMoneyMoving = R.id.nav_change_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query
    private val navMoneyMoving = R.id.nav_money_moving
    private val navCategories = R.id.nav_categories
    private val navCashAccount = R.id.nav_cash_account
    private val navCurrencies = R.id.nav_currencies

    fun checkAndSaveToSP(
        navControlHelper: NavControlHelper,
        argsForQuery: String,
        argsForSelect: String,
        id: Int?
    ) {
        when (navControlHelper.previousFragment()) {
            navMoneyMoving -> {
                saveToSP(argsForQuery,id)
            }
            navMoneyMovingQuery -> {
                saveToSP(argsForQuery,id)
            }
            navNewMoneyMoving -> {
                saveToSP(argsForSelect,id)
            }
        }
    }
    fun checkAndSaveToSP(
        navControlHelper: NavControlHelper,
        argsForQuery: String,
        argsForSelect: String,
        argsIncomeSpending:String,
        id: Int?
    ) {
        when (navControlHelper.previousFragment()) {
            navMoneyMoving -> {
                saveToSP(argsForQuery,id)
            }
            navMoneyMovingQuery -> {
                saveToSP(argsForQuery,id)
            }
            navNewMoneyMoving -> {
                saveToSP(argsForSelect,id)
            }
        }
    }

    private fun saveToSP(
        argsCurrencyKey: String,
        value: Int?
    ) {
        spEditor.putInt(argsCurrencyKey, value ?: -1)
        spCommit()
    }

    fun saveIsIncomeCategoryToSP(argsIncomeSpending: String, value: String) {
            spEditor.putString(argsIncomeSpending,value)
            spCommit()
            Log.i("TAG","---$value---")
    }
    private fun spCommit(){
        spEditor.commit()
    }
}