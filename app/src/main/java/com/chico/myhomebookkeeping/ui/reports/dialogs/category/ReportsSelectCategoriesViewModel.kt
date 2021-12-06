package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import com.chico.myhomebookkeeping.helpers.Message

class ReportsSelectCategoriesViewModel(
    private var selectedCategoriesSet: MutableSet<Int> = mutableSetOf()
) {

    fun addCategoryInSetOfCategories(id: Int) {
        selectedCategoriesSet.add(id)
        getSetToString()
    }

    fun deleteCategoryInSetOfCategories(id: Int) {
        selectedCategoriesSet.remove(id)
        getSetToString()
    }

    private fun getSetToString() {
        Message.log( " selected items ${selectedCategoriesSet.joinToString ()}")
    }
}