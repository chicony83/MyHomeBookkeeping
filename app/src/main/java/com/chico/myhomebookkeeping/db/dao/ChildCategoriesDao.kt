package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.ParentCategory

@Dao
interface ChildCategoriesDao {

    @Insert
    suspend fun addNewChildCategory(childCategory: ChildCategory):Long

    @Insert
    suspend fun addChildCategories(childCategories: List<ChildCategory>)

    @Query("SELECT * FROM child_categories_table  ORDER BY name_res ASC")
    suspend fun getAllChildCategoriesSortNameASC(): List<ChildCategory>

    @Query("SELECT * FROM child_categories_table  ORDER BY name_res DESC")
    suspend fun getAllChildCategoriesSortNameDESC(): List<ChildCategory>

    @Query("SELECT * FROM child_categories_table  ORDER BY id ASC")
    suspend fun getAllChildCategoriesSortIdASC(): List<ChildCategory>

    @Query("SELECT * FROM child_categories_table  ORDER BY id DESC")
    suspend fun getAllChildCategoriesSortIdDESC(): List<ChildCategory>

    @Query("SELECT * FROM child_categories_table WHERE id = :childId")
    suspend fun getOneChildCategory(childId: Int): ChildCategory

    @Query("UPDATE child_categories_table SET name_res = :nameRes, icon_res = :iconResource, parent_category_name_res = :parentNameRes WHERE id = :id")
    suspend fun changeChildCategory(
        id: Long,
        nameRes: Int,
        iconResource: Int,
        parentNameRes:Int
    ):Int
}