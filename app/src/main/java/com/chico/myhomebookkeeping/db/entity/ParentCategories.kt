package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_categories_table")
class ParentCategories(
    @ColumnInfo(name = "parent_category_name")
    val name:String,
    @ColumnInfo(name = "name_icon_parent_category")
    val icon:Int?,
    @ColumnInfo(name = "parent_category_order")
    val parentCategoryOrder: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}
