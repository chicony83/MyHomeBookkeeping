package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.CashAccount

@Dao
interface CashAccountDao {
    @Insert
    suspend fun addCashAccount(cashAccount: CashAccount):Long

    @Query("SELECT COUNT(*) FROM cash_account_table")
    suspend fun getCashAccountsCount(): Int

    @Query("SELECT * FROM cash_account_table ORDER BY cash_account_name ASC")
    suspend fun getAllCashAccountsSortNameAsc(): List<CashAccount>

    @Query("SELECT * FROM cash_account_table ORDER BY cashAccountId ASC")
    suspend fun getAllCashAccountsSortIdAsc():List<CashAccount>

    @Query("SELECT * FROM cash_account_table WHERE is_cash_account_default = 1 ORDER BY cashAccountId ASC LIMIT 1")
    suspend fun getDefaultCashAccount(): CashAccount?

    @Query("SELECT * FROM cash_account_table ORDER BY cashAccountId ASC LIMIT 1")
    suspend fun getFirstCashAccount(): CashAccount?

    @Query("UPDATE cash_account_table SET is_cash_account_default = CASE WHEN cashAccountId = :id THEN 1 ELSE 0 END")
    suspend fun setDefaultCashAccount(id: Int): Int

    @Query("SELECT * FROM cash_account_table WHERE cashAccountId = :id")
    suspend fun getOneCashAccount(id: Int): CashAccount

    @Query("UPDATE cash_account_table SET cash_account_name =:name, cash_account_number = :number WHERE cashAccountId = :id")
    suspend fun changeLine(id: Int, name: String, number: String):Int
}
