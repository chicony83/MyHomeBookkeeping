package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chico.myhomebookkeeping.db.entity.BankAccount

@Dao
interface BankAccountDao {
    @Insert
    suspend fun addBankAccount(bankAccount: BankAccount)

    @Query ("SELECT * FROM bank_account_table")
    suspend fun getAllBAnkAccounts():List<BankAccount>
}