package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_account_table")
data class BankAccount(
    @ColumnInfo(name = "account_name")
    val accountName: String,
    @ColumnInfo(name = "is_cash")
    val isCash: Boolean,
    @ColumnInfo(name = "bank_account_number")
    val bankAccountNumber: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}