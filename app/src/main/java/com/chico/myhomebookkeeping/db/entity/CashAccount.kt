package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_account_table")
data class CashAccount(
    @ColumnInfo(name = "account_name")
    val accountName: String,
    @ColumnInfo(name = "cash_account_number")
    val bankAccountNumber: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}