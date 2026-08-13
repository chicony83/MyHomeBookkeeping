package com.chico.myhomebookkeeping.ui.reports.selectCategories

data class ReportsCategoriesItem(
    val id: Int,
    val name: String,
    val icon: Int?,
    var isChecked: Boolean
)
