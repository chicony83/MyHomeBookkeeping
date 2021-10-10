package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.utils.launchForResult

object CategoriesUseCase {
    suspend fun addNewCategory(
        db: CategoryDao,
        newCategory: Categories
    ): Long {
        return db.addCategory(newCategory)
    }

    suspend fun getOneCategory(
        db: CategoryDao,
        id: Int
    ): Categories? {
        return launchForResult {
            db.getOneCategory(id)
        }
    }

    suspend fun changeCategoryLine(
        db: CategoryDao,
        id: Int,
        name: String,
        isIncome: Boolean
    ): Int {
        return db.changeLine(id, name, isIncome)
    }

    suspend fun getAllCategoriesSortIdDesc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesIdDESC()
    }

    suspend fun getAllCategoriesSortIdAsc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesIdASC()
    }

    suspend fun getAllCategoriesSortNameAsc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesNameASC()
    }

    suspend fun getAllCategoriesSortNameDesc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesNameDESC()
    }
}