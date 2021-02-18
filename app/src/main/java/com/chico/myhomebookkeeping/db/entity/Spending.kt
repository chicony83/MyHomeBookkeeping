package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spending_category_table")
data class Spending(
    @ColumnInfo(name = "spending")
    val spendingCategory: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}