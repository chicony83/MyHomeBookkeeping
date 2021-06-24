package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.CashAccount

@Dao
interface CashAccountDao {
    @Insert
    suspend fun addCashAccount(cashAccount: CashAccount)

    @Query("SELECT * FROM cash_account_table ORDER BY cashAccountId DESC")
    suspend fun getAllCashAccounts(): List<CashAccount>

    @Query("SELECT * FROM cash_account_table WHERE cashAccountId = :id")
    suspend fun getOneCashAccount(id: Int): CashAccount

    @Query("UPDATE cash_account_table SET cash_account_name =:name AND cash_account_number = :number WHERE cashAccountId = :id")
    suspend fun changeLine(id: Int, name: String, number: Int?)
}