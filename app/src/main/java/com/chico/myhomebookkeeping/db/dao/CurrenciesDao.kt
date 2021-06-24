package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chico.myhomebookkeeping.db.entity.Currencies

@Dao
interface CurrenciesDao {

    @Insert
    suspend fun addCurrency(currency: Currencies)

    @Query("SELECT * FROM currency_table ORDER BY currencyId DESC")
    suspend fun getAllCurrency():List<Currencies>

    @Query("SELECT * FROM currency_table WHERE currencyId = :id")
    suspend fun getOneCurrency(id:Int):Currencies

    @Query("UPDATE currency_table SET currency_name = :name WHERE currencyId = :id")
    suspend fun changeLine(id: Int, name: String)
}