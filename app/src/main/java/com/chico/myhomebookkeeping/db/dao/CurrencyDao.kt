package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface CurrencyDao {
    @Insert
    suspend fun addCurrency(currency: Currency)

    @Query("SELECT * FROM currency_table")
    suspend fun getAllCurrency():List<Currency>
}