package com.chico.myhomebookkeeping.ui.reports.selectCategories

data class ReportsCategoriesItem(
    val id: Int,
    val name: String,
    val amount: String,
    val isIncome: Boolean,
    var isChecked: Boolean
)