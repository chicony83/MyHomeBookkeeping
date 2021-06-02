package com.chico.myhomebookkeeping.helpers

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.R

class SaveARGS(private val spEditor: SharedPreferences.Editor) {
    private val navNewMoneyMoving = R.id.nav_new_money_moving
    private val navMoneyMovingQuery = R.id.nav_money_moving_query
    private val navMoneyMoving = R.id.nav_money_moving

    fun checkAndSaveSP(
        controlHelper: ControlHelper,
        argsForQuery: String,
        argsForSelect: String,
        id: Int?
    ) {
        when (controlHelper.previousFragment()) {
            navMoneyMoving -> {
                saveSP(argsForSelect,id)
            }
            navMoneyMovingQuery -> {
                saveSP(argsForQuery,id)
            }
            navNewMoneyMoving -> {
                saveSP(argsForSelect,id)
            }
        }
    }

    private fun saveSP(
        argsCurrencyKey: String,
        _selected: Int?
    ) {
        spEditor.putInt(argsCurrencyKey, _selected ?: -1)
        spEditor.commit()
    }
}