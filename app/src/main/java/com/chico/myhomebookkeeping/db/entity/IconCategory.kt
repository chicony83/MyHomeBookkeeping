package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_category_table")
data class IconCategory(
    @ColumnInfo(name = "name")
    val iconCategoryName:String
) {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}