package com.chico.myhomebookkeeping.ui.reports.main

data class ReportCategoryItem(
    val categoryId: Int,
    val displayName: String,
    val iconKey: String?,
    val amount: Double,
    val percentage: Double,
    val color: Int
)
