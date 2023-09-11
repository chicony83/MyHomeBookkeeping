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

    suspend fun changeCategoryLineWithoutParentCategory(
        db: CategoryDao,
        id: Int,
        name: String,
        isIncome: Boolean,
        iconResource: Int
    ): Int {
        return db.changeLineWithoutCategory(id, name, isIncome,iconResource)
    }

    suspend fun changeCategoryLineFull(
        db: CategoryDao,
        id: Int,
        name: String,
        isIncome: Boolean,
        iconResource: Int,
        parentCategoryId: Int
    ):Int {
        return db.changeLineFull(id,name,isIncome,iconResource,parentCategoryId)
    }

    suspend fun getAllCategoriesSortIdDesc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesSortIdDESC()
    }

    suspend fun getAllCategoriesSortIdAsc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesSortIdASC()
    }

    suspend fun getAllCategoriesSortNameAsc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesSortNameASC()
    }

    suspend fun getAllCategoriesSortNameDesc(db: CategoryDao): List<Categories> {
        return db.getAllCategoriesSortNameDESC()
    }

    suspend fun getAllCategoriesWithParentIdSortNameAsc(
        parentCategoryId: Int,
        db: CategoryDao
    ): List<Categories>? {
        return db.getAllCategoriesWithParentIdSortNameAsc(parentCategoryId)
    }

}