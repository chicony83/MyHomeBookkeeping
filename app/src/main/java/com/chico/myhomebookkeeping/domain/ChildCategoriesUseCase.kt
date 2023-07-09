package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.ChildCategoriesDao
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.utils.launchForResult

object ChildCategoriesUseCase {
    suspend fun addNewCategory(
        db: ChildCategoriesDao,
        newCategory: ChildCategory
    ): Long {
        return db.addNewChildCategory(newCategory)
    }

    suspend fun getOneCategory(
        db: ChildCategoriesDao,
        id: Int
    ): ChildCategory? {
        return launchForResult {
            db.getOneChildCategory(id)
        }
    }

    suspend fun changeCategoryLine(
        db: ChildCategoriesDao,
        id: Long,
        nameRes: Int,
        parentNameRes: Int,
        iconResource: Int
    ): Int {
        return db.changeChildCategory(
            id = id,
            nameRes = nameRes,
            iconResource = iconResource,
            parentNameRes = parentNameRes
        )
    }

    suspend fun getAllCategoriesSortIdDesc(db: ChildCategoriesDao): List<ChildCategory> {
        return db.getAllChildCategoriesSortIdDESC()
    }

    suspend fun getAllCategoriesSortIdAsc(db: ChildCategoriesDao): List<ChildCategory> {
        return db.getAllChildCategoriesSortIdASC()
    }

    suspend fun getAllCategoriesSortNameAsc(db: ChildCategoriesDao): List<ChildCategory> {
        return db.getAllChildCategoriesSortNameASC()
    }

    suspend fun getAllCategoriesSortNameDesc(db: ChildCategoriesDao): List<ChildCategory> {
        return db.getAllChildCategoriesSortNameDESC()
    }
}