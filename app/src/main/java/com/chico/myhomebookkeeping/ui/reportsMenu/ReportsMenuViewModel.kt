package com.chico.myhomebookkeeping.ui.reportsMenu

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants

class ReportsMenuViewModel(
    val app: Application
) : AndroidViewModel(app) {
    fun saveArgs(report: String) {
        Message.log("ARGS Saved, reports type = $report")
    }

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()
}