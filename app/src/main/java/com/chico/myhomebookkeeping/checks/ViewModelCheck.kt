package com.chico.myhomebookkeeping.checks

import android.content.SharedPreferences
import android.os.Bundle

class ViewModelCheck(private val sharedPreferences: SharedPreferences) {

    fun getValueSP(argsKey: String): Int {
        return sharedPreferences.getInt(argsKey, -1)
    }

    fun getValueBundle(arguments: Bundle?, args: String): Int {
        return arguments?.getInt(args) ?: -1
    }

    fun isPositiveValue(value: Int): Boolean {
        return value > 0
    }

}