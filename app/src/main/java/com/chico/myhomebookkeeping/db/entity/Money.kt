package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_moving_table")
data class Money(
    @ColumnInfo(name = "event_time")
    val time: Long,
    @ColumnInfo(name = "is_income")
    val isIncome: Boolean,
    @ColumnInfo(name = "is_spending")
    val isSpending: Boolean,
    @ColumnInfo(name = "income_category")
    val incomeCategory: Float,
    @ColumnInfo(name = "spending_category")
    val spendingCategory: Float,
    @ColumnInfo(name = "sum_of_money")
    val sumOfMoney: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "place")
    val place: String,
    @ColumnInfo(name = "gps")
    val gps: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}