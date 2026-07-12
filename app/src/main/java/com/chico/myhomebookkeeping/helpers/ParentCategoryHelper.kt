package com.chico.myhomebookkeeping.helpers

import com.chico.myhomebookkeeping.db.entity.ParentCategories

object ParentCategoryHelper {
    fun getIdSelectedParentCategory(
        name: String,
        parentCategoriesList: List<ParentCategories>
    ): Int? = parentCategoriesList.firstOrNull { it.name == name }?.id
}
