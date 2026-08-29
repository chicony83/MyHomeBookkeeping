package com.chico.myhomebookkeeping.ui.reports.selectCategories

data class ReportsCategoriesItem(
    val id: Int,
    val name: String,
    val iconKey: String?,
    val categoryIds: Set<Int>,
    val incomeCategoryIds: Set<Int>,
    val spendingCategoryIds: Set<Int>,
    var isChecked: Boolean,
    val isPartiallyChecked: Boolean = false
)
