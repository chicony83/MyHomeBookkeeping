package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_category_table")
data class Income(
    @ColumnInfo(name = "income_category")
    val incomeCategory: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
