package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.ui.categories.CategoriesViewModel
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object CategoriesUseCase {
    suspend fun getOneCategory(db: CategoryDao, id: Int):String {
        return launchForResult {
            db.getOneCategory(id).first().categoryName
        }.toString()
    }

    fun addNewCategoryRunBlocking(
        db: CategoryDao,
        newCategory: Categories,
        categoriesViewModel: CategoriesViewModel
    ) = runBlocking {
        db.addCategory(newCategory)
        categoriesViewModel.loadCategories()
    }
}