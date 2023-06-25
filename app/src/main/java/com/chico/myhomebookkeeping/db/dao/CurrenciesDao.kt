package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.Currencies

@Dao
interface CurrenciesDao {

    @Insert
    suspend fun addCurrency(currency: Currencies): Long

    @Insert
    suspend fun addCurrencies(currencies: List<Currencies>)

    @Query("SELECT * FROM currency_table ORDER BY currency_name ASC")
    suspend fun getAllCurrenciesSortNameAsc(): List<Currencies>

    @Query("SELECT * FROM currency_table WHERE currencyId = :id")
    suspend fun getOneCurrency(id: Int): Currencies

    @Query("UPDATE currency_table SET currency_name = :name WHERE currencyId = :id")
    suspend fun changeLine(id: Int, name: String): Int

    @Query("UPDATE currency_table SET currency_name = :name,currency_name_short = :shortName,iso_4217 = :iSO  WHERE currencyId = :id")
    suspend fun changeLineCurrencyNameShortNameIso(
        id: Int,
        name: String,
        shortName: String?,
        iSO: String?
    ): Int
}