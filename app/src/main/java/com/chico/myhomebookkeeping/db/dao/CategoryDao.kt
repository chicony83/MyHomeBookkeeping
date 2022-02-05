package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories

@Dao
interface CategoryDao {

    @Insert
    suspend fun addCategory(category: Categories):Long

    @Query("SELECT * FROM category_table ORDER BY category_name ASC")
    suspend fun getAllCategoriesSortNameASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY category_name DESC")
    suspend fun getAllCategoriesSortNameDESC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId ASC")
    suspend fun getAllCategoriesSortIdASC(): List<Categories>

    @Query("SELECT * FROM category_table ORDER BY categoriesId DESC")
    suspend fun getAllCategoriesSortIdDESC(): List<Categories>

    @Query("SELECT * FROM category_table WHERE categoriesId = :id")
    suspend fun getOneCategory(id:Int):Categories

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome, icon_category = :iconResource WHERE categoriesId = :id")
    suspend fun changeLine(id: Int, name: String, isIncome: Boolean,iconResource:Int):Int
}