package com.chico.myhomebookkeeping.db.dao

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface BankAccountDao {
    @Insert
    suspend fun add
}