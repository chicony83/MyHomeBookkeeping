package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.db.entity.IconCategory

object IconCategoriesUseCase {

    suspend fun addIconCategory(db: IconCategoryDao, iconCategory: IconCategory): Long {
        return db.addNewIconCategory(iconCategory)
    }

    suspend fun getAllIconCategories(db: IconCategoryDao): List<IconCategory> {
        return db.getAllIconCategories()
    }

}