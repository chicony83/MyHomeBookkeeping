package com.chico.myhomebookkeeping.ui.reports.items

data class ReportsCategoriesItem(
    val id: Int,
    val name: String,
    val amount: Double,
    var isChecked: Boolean,
    val isIncome: Boolean
)