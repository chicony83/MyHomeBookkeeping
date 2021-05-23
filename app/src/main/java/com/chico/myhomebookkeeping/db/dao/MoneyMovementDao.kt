package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

@Dao
interface MoneyMovementDao {

    @Insert
    suspend fun addMovingMoney(moneyMovement: MoneyMovement): Long

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney(): List<MoneyMovement>

    @Query("SELECT time_stamp,amount, account_name AS cash_account_name_value, currency_name AS currency_name_value,category_name AS category_name_value, is_income FROM money_moving_table,cash_account_table,currency_table,category_table WHERE cash_account == cashAccountId AND currency == currencyId AND category== categoriesId")
    suspend fun getFullMoneyMoving(): List<FullMoneyMoving>

    @RawQuery
    suspend fun getSelectedMoneyMoving(query:SimpleSQLiteQuery):List<FullMoneyMoving>
}