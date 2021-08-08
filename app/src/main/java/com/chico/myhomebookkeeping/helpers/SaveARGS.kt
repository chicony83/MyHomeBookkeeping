package com.chico.myhomebookkeeping.helpers

import android.content.SharedPreferences
import android.util.Log
import com.chico.myhomebookkeeping.R

class SaveARGS(private val spEditor: SharedPreferences.Editor) {
    private val navNewMoneyMoving = R.id.nav_new_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query
    private val navMoneyMoving = R.id.nav_money_moving
    private val navChangeMoneyMoving = R.id.nav_change_money_moving

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
        argsKey: String,
        value: Int?
    ) {
        messageLog( argsKey, value.toString())
        spEditor.putInt(argsKey, value ?: -1)
        spCommit()
    }

    fun saveToSP(
        argsKey: String,
        value: String?
    ) {
        messageLog( argsKey, value.toString())
        spEditor.putString(argsKey, value ?: "")
        spCommit()
    }

    fun saveToSP(
        argsKey: String,
        value: Long?
    ) {
        messageLog( argsKey, value.toString())
        spEditor.putLong(argsKey, value ?: -1)
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
}