package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.ParentCategories

@Dao
interface ParentCategoriesDao {

    @Insert
    suspend fun addNewParentCategory(parentCategories: ParentCategories):Long

    @Query("SELECT * FROM parent_categories_table ORDER BY parent_category_order ASC, parent_category_name ASC")
    suspend fun getAllParentCategoriesSortNameAsc():List<ParentCategories>

    @Query("SELECT * FROM parent_categories_table WHERE id = :id")
    suspend fun getSelectedParentCategory(id: Int): ParentCategories

    @Query("UPDATE parent_categories_table SET parent_category_order = :order WHERE id = :id")
    suspend fun updateParentCategoryOrder(id: Int, order: Int): Int
}
