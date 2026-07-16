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

    @Query("SELECT COUNT(*) FROM currency_table")
    suspend fun getCurrenciesCount(): Int

    @Query("SELECT * FROM currency_table ORDER BY currency_name ASC")
    suspend fun getAllCurrenciesSortNameAsc(): List<Currencies>

    @Query("SELECT * FROM currency_table WHERE is_currency_default = 1 ORDER BY currencyId ASC LIMIT 1")
    suspend fun getDefaultCurrency(): Currencies?

    @Query("SELECT * FROM currency_table ORDER BY currencyId ASC LIMIT 1")
    suspend fun getFirstCurrency(): Currencies?

    @Query("UPDATE currency_table SET is_currency_default = CASE WHEN currencyId = :id THEN 1 ELSE 0 END")
    suspend fun setDefaultCurrency(id: Int): Int

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
