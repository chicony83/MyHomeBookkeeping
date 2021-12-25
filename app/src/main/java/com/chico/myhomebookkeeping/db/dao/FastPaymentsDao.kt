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
    suspend fun addBlank(newFastPayment: FastPayments):Long

    @Query("SELECT * FROM fast_payments_table")
    suspend fun getAllFastPayments():List<FastPayments>

    @RawQuery
    fun getAllFullFastPayments(query: SimpleSQLiteQuery):List<FullFastPayment>
}