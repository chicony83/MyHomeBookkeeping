package com.chico.myhomebookkeeping.db.dao

import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Money

interface MoneyDao {

    @Insert
    suspend fun addMovingMoney(money:Money)

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney():List<Money>
}