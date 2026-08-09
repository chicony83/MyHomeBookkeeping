package com.chico.myhomebookkeeping.ui.reports.main

data class ReportCategoryItem(
    val categoryId: Int,
    val displayName: String,
    val iconRes: Int?,
    val amount: Double,
    val percentage: Double,
    val color: Int
)
