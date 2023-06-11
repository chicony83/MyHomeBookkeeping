package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategory

@Dao
interface ParentCategoriesDao {

    @Insert
    suspend fun addNewParentCategory(parentCategory: ParentCategory):Long

    @Insert
    suspend fun addParentCategories(parentCategories: List<ParentCategory>)

    @Query("SELECT * FROM parent_categories_table ORDER BY name_res ASC")
    suspend fun getAllParentCategoriesSortNameASC(): List<ParentCategory>

    @Query("SELECT * FROM parent_categories_table ORDER BY name_res DESC")
    suspend fun getAllParentCategoriesSortNameDESC(): List<ParentCategory>

    @Query("SELECT * FROM parent_categories_table ORDER BY categoriesId ASC")
    suspend fun getAllParentCategoriesSortIdASC(): List<ParentCategory>

    @Query("SELECT * FROM parent_categories_table ORDER BY categoriesId DESC")
    suspend fun getAllParentCategoriesSortIdDESC(): List<ParentCategory>

    @Query("SELECT * FROM parent_categories_table WHERE categoriesId = :id")
    suspend fun getOneParentCategory(id: Long): ParentCategory

    @Query("UPDATE parent_categories_table SET name_res = :nameRes, is_income = :isIncome, icon_res = :iconResource WHERE categoriesId = :id")
    suspend fun changeParentCategory(
        id: Long,
        nameRes: Int,
        isIncome: Boolean,
        iconResource: Int
    ):Int
}