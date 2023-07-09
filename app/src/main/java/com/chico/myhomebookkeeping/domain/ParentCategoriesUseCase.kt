package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.ChildCategoriesDao
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.ParentCategory
import com.chico.myhomebookkeeping.utils.launchForResult

object ParentCategoriesUseCase {
    suspend fun addNewCategory(
        db: ParentCategoriesDao,
        newCategory: ParentCategory
    ): Long {
        return db.addNewParentCategory(newCategory)
    }

    suspend fun getOneCategory(
        db: ParentCategoriesDao,
        id: Long
    ): ParentCategory? {
        return launchForResult {
            db.getOneParentCategory(id)
        }
    }

    suspend fun changeCategoryLine(
        db: ParentCategoriesDao,
        id: Long,
        nameRes: Int,
        isIncome: Boolean,
        iconResource: Int
    ): Int {
        return db.changeParentCategory(
            id = id,
            nameRes = nameRes,
            iconResource = iconResource,
            isIncome = isIncome
        )
    }

    suspend fun getAllCategoriesSortIdDesc(db: ParentCategoriesDao): List<ParentCategory> {
        return db.getAllParentCategoriesSortIdDESC()
    }

    suspend fun getAllCategoriesSortIdAsc(db: ParentCategoriesDao): List<ParentCategory> {
        return db.getAllParentCategoriesSortIdASC()
    }

    suspend fun getAllCategoriesSortNameAsc(db: ParentCategoriesDao): List<ParentCategory> {
        return db.getAllParentCategoriesSortNameASC()
    }

    suspend fun getAllCategoriesSortNameDesc(db: ParentCategoriesDao): List<ParentCategory> {
        return db.getAllParentCategoriesSortNameDESC()
    }
}