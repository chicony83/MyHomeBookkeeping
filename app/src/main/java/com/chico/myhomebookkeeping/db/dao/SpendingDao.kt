package com.chico.myhomebookkeeping.db.dao

import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Spending

interface SpendingDao {

    @Insert
    suspend fun addSpendingMoneyCategory(spending: Spending)

    @Query("SELECT * FROM spending_category_table")
    suspend fun getAllSpendingCategory():List<Spending>

}