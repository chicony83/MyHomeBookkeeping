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
    val currency: Int,
    @ColumnInfo(name = "category")
    val category: Int? = null,
    @ColumnInfo(name = "category_user_name")
    val categoryUserName: String? = null,
    @ColumnInfo(name = "child_category_name_res")
    val childCategoryNameResValue: Int? = null,
    @ColumnInfo(name = "child_category_user_name")
    val childCategoryUserName: String? = null,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "description")
    val description: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}