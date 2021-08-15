package com.chico.myhomebookkeeping.checks

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.constants.Constants

class GetSP(private val sharedPreferences: SharedPreferences) {
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsIncome = Constants.FOR_QUERY_INCOME
    private val argsSpending = Constants.FOR_QUERY_SPENDING
    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    fun getInt(argsKey: String): Int {
        return sharedPreferences.getInt(argsKey, minusOneInt)
    }

    fun getLong(argsKey: String): Long {
        return sharedPreferences.getLong(argsKey, minusOneLong)
    }

    fun getString(argsKey: String): String? {
        return sharedPreferences.getString(argsKey, argsNone)
    }
    fun getBoolean(argsKey:String):Boolean{
        return sharedPreferences.getBoolean(argsKey,true)
    }

    fun isCategoryNone(argsIncomeSpending: String): Boolean {
        return sharedPreferences.getString(argsIncomeSpending, argsNone).toString() == argsNone
    }

    fun isCategoryIncome(argsIncomeSpending: String): Boolean {
        return sharedPreferences.getString(argsIncomeSpending, argsNone).toString() == argsIncome
    }

    fun isCategorySpending(argsIncomeSpending: String): Boolean {
        return sharedPreferences.getString(argsIncomeSpending, argsNone).toString() == argsSpending
    }
}