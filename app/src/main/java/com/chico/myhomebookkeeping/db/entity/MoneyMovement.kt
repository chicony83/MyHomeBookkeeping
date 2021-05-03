package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_moving_table")
data class MoneyMovement(
    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long,
    @ColumnInfo(name = "cash_account")
    val cashAccount: Int,
    @ColumnInfo(name = "currency")
    val currency:Int,
    @ColumnInfo(name = "category")
    val category:Int,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "description")
    val description: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}