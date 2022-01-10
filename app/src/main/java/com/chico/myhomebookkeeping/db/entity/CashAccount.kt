package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_account_table")
data class CashAccount(
    @ColumnInfo(name = "cash_account_name")
    val accountName: String,
    @ColumnInfo(name = "cash_account_number")
    val bankAccountNumber: String,
    @ColumnInfo(name = "icon_cash_account")
    val icon:Int?
) {
    @PrimaryKey(autoGenerate = true)
    var cashAccountId: Int? = null
}