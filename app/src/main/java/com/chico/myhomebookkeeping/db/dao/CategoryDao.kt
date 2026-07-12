package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories

@Dao
interface CategoryDao {

    @Insert
    suspend fun addCategory(category: Categories): Long

    @Query("SELECT * FROM category_table ORDER BY category_name ASC")
    suspend fun getAllCategoriesSortNameASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY category_order ASC, category_name ASC")
    suspend fun getAllCategoriesSortOrderASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY category_name DESC")
    suspend fun getAllCategoriesSortNameDESC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId ASC")
    suspend fun getAllCategoriesSortIdASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId DESC")
    suspend fun getAllCategoriesSortIdDESC(): List<Categories>

    @Query("SELECT * FROM category_table WHERE categoriesId = :id")
    suspend fun getOneCategory(id: Int): Categories

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome, icon_category = :iconResource, parent_category_id = NULL WHERE categoriesId = :id")
    suspend fun changeLineWithoutCategory(
        id: Int,
        name: String,
        isIncome: Boolean,
        iconResource: Int
    ): Int

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome, icon_category = :iconResource, parent_category_id = :parentCategoryId WHERE categoriesId = :id")
    suspend fun changeLineFull(
        id: Int,
        name: String,
        isIncome: Boolean,
        iconResource: Int,
        parentCategoryId: Int
    ):Int

    @Query("SELECT * FROM category_table WHERE parent_category_id = :parentCategoryId ORDER BY category_name ASC")
    suspend fun getAllCategoriesWithParentIdSortNameAsc(parentCategoryId: Int): List<Categories>?

    @Query("SELECT * FROM category_table WHERE parent_category_id IS null ORDER BY category_name ASC")
    suspend fun getAllCategoriesWithoutParentCategory(): List<Categories>?

    @Query("UPDATE category_table SET category_order = :order WHERE categoriesId = :id")
    suspend fun updateCategoryOrder(id: Int, order: Int): Int

    @Query("UPDATE category_table SET parent_category_id = :parentCategoryId, category_order = :order WHERE categoriesId = :id")
    suspend fun updateCategoryParentAndOrder(id: Int, parentCategoryId: Int?, order: Int): Int

}
