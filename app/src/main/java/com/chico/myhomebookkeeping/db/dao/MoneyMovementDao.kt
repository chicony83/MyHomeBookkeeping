package com.chico.myhomebookkeeping.db.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

@Dao
interface MoneyMovementDao {

    @Insert
    suspend fun addMovingMoney(moneyMovement: MoneyMovement): Long

    @Transaction
    suspend fun addTransfer(source: MoneyMovement, destination: MoneyMovement): List<Long> {
        return listOf(addMovingMoney(source), addMovingMoney(destination))
    }

    @Query("SELECT * FROM money_moving_table WHERE id = :id")
    suspend fun getOneMoneyMoving(id:Long):MoneyMovement

    @Query("SELECT * FROM money_moving_table")
    suspend fun getAllMovingMoney(): List<MoneyMovement>

    @Query("SELECT money_moving_table.id, time_stamp, amount, cash_account_name AS cash_account_name_value, currency_name AS currency_name_value, category_name AS category_name_value, money_moving_table.payment_type_id = 0 AS is_income, money_moving_table.payment_type_id, payment_type_name, transfer_group_id, transfer_direction, description FROM money_moving_table INNER JOIN cash_account_table ON cash_account == cashAccountId INNER JOIN currency_table ON currency == currencyId INNER JOIN payment_type_table ON money_moving_table.payment_type_id == payment_type_table.id LEFT JOIN category_table ON category == categoriesId ORDER BY money_moving_table.id DESC")
    suspend fun getFullMoneyMoving(): List<FullMoneyMoving>

    @RawQuery
    suspend fun getSelectedMoneyMoving(query:SimpleSQLiteQuery):List<FullMoneyMoving>

    @RawQuery
    suspend fun getSelectedFullMoneyMoving(query: SimpleSQLiteQuery):List<FullMoneyMoving>

    @RawQuery
    suspend fun getOneFullMoneyMoving(query: SimpleSQLiteQuery): FullMoneyMoving

    @Query("UPDATE money_moving_table SET description=:description,currency=:currencyId,category=:categoryId,payment_type_id=:paymentTypeId,cash_account=:cashAccountId,amount = :amount,time_stamp = :dateTime WHERE id = :id")
    suspend fun changeMoneyMovingLine(
        id:Long,
        dateTime: Long,
        amount: Double,
        cashAccountId: Int,
        categoryId: Int?,
        paymentTypeId: Int,
        currencyId: Int,
        description: String
    ): Int

    @Query("DELETE FROM money_moving_table WHERE id = :id")
    suspend fun deleteLine(id: Long):Int
}
