package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.FastPayments

@Dao
interface FastPaymentsDao {
    @Insert
    suspend fun addBlank(newFastPayment: FastPayments):Long

    @Query("SELECT * FROM fast_payments_table")
    suspend fun getAllFastPayments():List<FastPayments>
}