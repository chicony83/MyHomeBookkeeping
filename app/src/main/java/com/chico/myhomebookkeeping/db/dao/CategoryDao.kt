package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Categories

@Dao
interface CategoryDao {

    @Insert
    suspend fun addCategory(category: Categories)

    @Query("SELECT * FROM category_table")
    suspend fun getAllCategory(): List<Categories>

    @Query("SELECT * FROM category_table WHERE id = :id")
    suspend fun getOneCategory(id:Int):List<Categories>
}