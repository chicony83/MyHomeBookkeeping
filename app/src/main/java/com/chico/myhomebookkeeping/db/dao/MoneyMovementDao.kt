package com.chico.myhomebookkeeping.db.dao

import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

interface MoneyMovementDao {

    @Insert
    suspend fun addMovingMoney(money:MoneyMovement)

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney():List<MoneyMovement>
}