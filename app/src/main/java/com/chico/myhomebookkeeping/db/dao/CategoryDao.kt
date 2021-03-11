package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Category

@Dao
interface CategoryDao {

    @Insert
    suspend fun addIncomingMoneyCategory(category: Category)

    @Query("SELECT * FROM category_table")
    suspend fun getAllIncomeMoneyCategory():List<Category>
}