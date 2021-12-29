package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullFastPayment
import com.chico.myhomebookkeeping.db.entity.FastPayments

@Dao
interface FastPaymentsDao {
    @Insert
    suspend fun addBlank(newFastPayment: FastPayments): Long

    @Query("SELECT * FROM fast_payments_table")
    suspend fun getAllFastPayments(): List<FastPayments>

    @RawQuery
    suspend fun getAllFullFastPayments(query: SimpleSQLiteQuery): List<FullFastPayment>

    @RawQuery
    suspend fun getOneFullFastPayment(query: SimpleSQLiteQuery): FullFastPayment

    @Query("SELECT * FROM fast_payments_table WHERE id = :id")
    suspend fun getOneSelectedFastPayment(id: Long): FastPayments

    @Query("UPDATE fast_payments_table SET name_fast_payment = :name,rating = :rating, cash_account = :cashAccount,currency = :currency, category = :category, amount = :amount, description = :description WHERE id = :id")
    suspend fun changeFastPayment(
        id: Long,
        name: String,
        rating: Int,
        cashAccount: Int,
        currency: Int,
        category: Int,
        amount: Double,
        description: String
    ): Int

    @Query("DELETE FROM fast_payments_table WHERE id = :id")
    suspend fun deleteLine(id: Long): Int
}