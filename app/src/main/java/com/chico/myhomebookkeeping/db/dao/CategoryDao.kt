package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories

@Dao
interface CategoryDao {

    @Insert
    suspend fun addCategory(category: Categories)

    @Query("SELECT * FROM category_table ORDER BY categoriesId DESC")
    suspend fun getAllCategory(): List<Categories>

    @Query("SELECT * FROM category_table WHERE categoriesId = :id")
    suspend fun getOneCategory(id:Int):Categories

    @Query("UPDATE category_table SET category_name = :name, is_income = :isIncome WHERE categoriesId = :id")
    suspend fun changeLine(id: Int, name: String, isIncome: Boolean)
}