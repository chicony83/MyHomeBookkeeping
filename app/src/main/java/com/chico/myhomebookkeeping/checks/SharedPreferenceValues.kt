package com.chico.myhomebookkeeping.checks

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.constants.Constants

class SharedPreferenceValues(private val sharedPreferences: SharedPreferences) {
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsIncome = Constants.FOR_QUERY_INCOME
    private val argsSpending = Constants.FOR_QUERY_SPENDING
    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val spEditor = sharedPreferences.edit()

    fun getInt(argsKey: String): Int {
        return sharedPreferences.getInt(argsKey, minusOneInt)
    }

    fun getLong(argsKey: String): Long {
        return sharedPreferences.getLong(argsKey, minusOneLong)
    }

    fun getString(argsKey: String): String? {
        return sharedPreferences.getString(argsKey, argsNone)?:""
    }

    //    fun getValueBundle(arguments: Bundle?, args: String): Int {
//        return arguments?.getInt(args) ?: -1
//    }
    fun setLong(args: String, id: Long?) {
        spEditor.putLong(args, id ?: minusOneLong)
        spEditor.commit()
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