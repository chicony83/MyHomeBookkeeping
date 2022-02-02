package com.chico.myhomebookkeeping.db.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

@Dao
interface MoneyMovementDao {

    @Insert
    suspend fun addMovingMoney(moneyMovement: MoneyMovement): Long

    @Query("SELECT * FROM money_moving_table WHERE id = :id")
    suspend fun getOneMoneyMoving(id:Long):MoneyMovement

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney(): List<MoneyMovement>

    @Query("SELECT id,time_stamp,amount, cash_account_name AS cash_account_name_value, currency_name AS currency_name_value,category_name AS category_name_value, is_income FROM money_moving_table,cash_account_table,currency_table,category_table WHERE cash_account == cashAccountId AND currency == currencyId AND category== categoriesId ORDER BY id DESC")
    suspend fun getFullMoneyMoving(): List<FullMoneyMoving>

    @RawQuery
    suspend fun getSelectedMoneyMoving(query:SimpleSQLiteQuery):List<FullMoneyMoving>

    @RawQuery
    suspend fun getSelectedFullMoneyMoving(query: SimpleSQLiteQuery):List<FullMoneyMoving>

    @RawQuery
    suspend fun getOneFullMoneyMoving(query: SimpleSQLiteQuery): FullMoneyMoving

    @Query("UPDATE money_moving_table SET description=:description,currency=:currencyId,category=:categoryId,cash_account=:cashAccountId,amount = :amount,time_stamp = :dateTime WHERE id = :id")
    suspend fun changeMoneyMovingLine(
        id:Long,
        dateTime: Long,
        amount: Double,
        cashAccountId: Int,
        categoryId: Int,
        currencyId: Int,
        description: String
    ): Int

    @Query("DELETE FROM money_moving_table WHERE id = :id")
    suspend fun deleteLine(id: Long):Int
}