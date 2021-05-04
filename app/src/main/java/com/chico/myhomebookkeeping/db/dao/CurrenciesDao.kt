package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Currencies

@Dao
interface CurrenciesDao {

    @Insert
    suspend fun addCurrency(currency: Currencies)

    @Query("SELECT * FROM currency_table")
    suspend fun getAllCurrency():List<Currencies>

    @Query("SELECT * FROM currency_table WHERE currencyId = :id")
    suspend fun getOneCurrency(id:Int):Currencies
}