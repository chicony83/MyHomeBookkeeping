package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blanks_table")
data class BlankMoneyMovement(
    @ColumnInfo(name = "icon")
    val icon: Int,
    @ColumnInfo(name = "name")
    val blankName: String,
    @ColumnInfo(name = "is_important")
    val isImportant:String,
    @ColumnInfo(name = "cash_account")
    val cashAccountId: Int,
    @ColumnInfo(name = "currency")
    val currencyId: Int,
    @ColumnInfo(name = "category")
    val categoryId: Int,
    @ColumnInfo(name = "description")
    val description: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
