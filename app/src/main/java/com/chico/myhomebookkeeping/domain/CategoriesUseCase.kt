package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesUseCase {
    fun addNewCategory(db: CategoryDao, newCategory: Categories) {
        launchIo {
            db.addCategory(newCategory)
        }
    }
}