package com.chico.myhomebookkeeping.ui.reportsMenu

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.enums.Reports
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants

class ReportsMenuViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()

    private val argsReportType = Constants.REPORT_TYPE
//    private val argsIncomeCategoryPieReport: String = Reports.PieIncome.toString()
//    private val argsSpendingCategoryPieReport: String = Reports.PieSpending.toString()

    fun saveArgs(report: String) {
        Message.log("ARGS Saved, reports type = $report")
        spEditor.putString(argsReportType,report)
    }
}