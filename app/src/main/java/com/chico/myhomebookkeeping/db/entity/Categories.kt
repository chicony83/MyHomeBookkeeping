package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Categories(
    @ColumnInfo(name = "category_name")
    val categoryName: String,
    @ColumnInfo(name = "is_income")
    val isIncome:Boolean,
    @ColumnInfo(name = "is_spending")
    val isSpending:Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var categoriesId: Int? = null
}
