package com.chico.myhomebookkeeping.helpers

import com.chico.myhomebookkeeping.db.entity.ParentCategories

object ParentCategoryHelper {
    fun getIdSelectedParentCategory(
        name: String,
        parentCategoriesList: List<ParentCategories>
    ): Int {
        var result = -1
        for (i in 0..parentCategoriesList.size) {
            if (parentCategoriesList[i].name == name) {
                result = parentCategoriesList[i].id!!
                break
            }
        }
        return result
    }
}