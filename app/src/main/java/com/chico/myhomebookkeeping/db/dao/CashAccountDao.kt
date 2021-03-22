package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.CashAccount

@Dao
interface CashAccountDao {
    @Insert
    suspend fun addCashAccount(bankAccount: CashAccount)

    @Query ("SELECT * FROM cash_account_table")
    suspend fun getAllCashAccounts():List<CashAccount>
}