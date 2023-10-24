package com.chico.myhomebookkeeping.ui.categories.such

import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories

class SuchName {
    fun suchParentCategoryNameById(
        parentCategoriesList: List<ParentCategories>,
        category: Categories
    ): String {
        var name = "noCategoryName"
        for (i in parentCategoriesList.indices) {
            if (parentCategoriesList[i].id == category.parentCategoryId) {
                parentCategoriesList[i].let {
                    name = it.name
                }
            }
        }
        return name
    }
}