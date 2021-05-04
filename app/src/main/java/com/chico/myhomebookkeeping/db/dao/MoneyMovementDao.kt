package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

@Dao
interface MoneyMovementDao {

    @Insert
    suspend fun addMovingMoney(moneyMovement: MoneyMovement): Long

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney(): List<MoneyMovement>

    @Query("SELECT time_stamp,amount, account_name AS cash_account_name_value FROM money_moving_table,cash_account_table WHERE cash_account == cashAccountId")
    suspend fun getFullMoneyMoving():List<FullMoneyMoving>
}