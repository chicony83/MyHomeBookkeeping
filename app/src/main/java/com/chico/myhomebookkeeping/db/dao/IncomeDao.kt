package com.chico.myhomebookkeeping.db.dao

import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Income

interface IncomeDao {

    @Insert
    suspend fun addIncomingMoneyCategory(income: Income)

    @Query("SELECT * FROM income_category_table")
    suspend fun getAllIncomeMoneyCategory():List<Income>
}