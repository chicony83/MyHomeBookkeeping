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
    @ColumnInfo(name = "icon_category")
    val icon:Int?,
    @ColumnInfo(name = "parent_category_id")
    val parentCategoryId:Int?
) {
    @PrimaryKey(autoGenerate = true)
    var categoriesId: Int? = null
}
