package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Category
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesUseCase {
    fun addNewCategory(db: CategoryDao, addingCategory: Category) {
        launchIo {
            db.addIncomingMoneyCategory(addingCategory)
        }
    }
}