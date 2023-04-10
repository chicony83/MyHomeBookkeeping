package com.chico.myhomebookkeeping.ui.calc

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate.*
import com.google.gson.Gson

class MyPreferences(context: Context) {

    companion object {
        private const val KEY_HISTORY = "HISTORY"
        private const val KEY_PREVENT_PHONE_FROM_SLEEPING = "PREVENT_PHONE_FROM_SLEEPING"
        private const val KEY_HISTORY_SIZE = "HISTORY_SIZE"
        private const val KEY_NUMBER_PRECISION = "NUMBER_PRECISION"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private var history = preferences.getString(KEY_HISTORY, null)
        set(value) = preferences.edit().putString(KEY_HISTORY, value).apply()
    var preventPhoneFromSleepingMode = preferences.getBoolean(KEY_PREVENT_PHONE_FROM_SLEEPING, false)
        set(value) = preferences.edit().putBoolean(KEY_PREVENT_PHONE_FROM_SLEEPING, value).apply()
    var historySize = preferences.getString(KEY_HISTORY_SIZE, "100")
        set(value) = preferences.edit().putString(KEY_HISTORY_SIZE, value).apply()
    var numberPrecision = preferences.getString(KEY_NUMBER_PRECISION, "10")
        set(value) = preferences.edit().putString(KEY_NUMBER_PRECISION, value).apply()

    fun getHistory(): MutableList<History> {
        val gson = Gson()
        return if (preferences.getString(KEY_HISTORY, null) != null) {
            gson.fromJson(history, Array<History>::class.java).asList().toMutableList()
        } else {
            mutableListOf()
        }
    }

    fun saveHistory(context: Context, history: List<History>){
        val gson = Gson()
        val history2 = history.toMutableList()
        while (historySize!!.toInt() > 0 && history2.size > historySize!!.toInt()) {
            history2.removeAt(0)
        }
        MyPreferences(context).history = gson.toJson(history2) // Convert to json
    }
}
