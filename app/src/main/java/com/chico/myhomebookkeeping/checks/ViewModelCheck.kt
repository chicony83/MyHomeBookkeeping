package com.chico.myhomebookkeeping.checks

import android.content.SharedPreferences
import android.os.Bundle
import com.chico.myhomebookkeeping.constants.Constants

class ViewModelCheck(private val sharedPreferences: SharedPreferences) {
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsIncome = Constants.FOR_QUERY_INCOME
    private val argsSpending = Constants.FOR_QUERY_SPENDING

    fun getValueSP(argsKey: String): Int {
        return sharedPreferences.getInt(argsKey, -1)
    }

    fun getValueBundle(arguments: Bundle?, args: String): Int {
        return arguments?.getInt(args) ?: -1
    }

    fun isPositiveValue(value: Int): Boolean {
        return value > 0
    }

    fun getStringValueSP(argsKey: String): String? {
        return sharedPreferences.getString(argsKey, argsNone)
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