package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_categories_table")
class ParentCategories(
    @ColumnInfo(name = "name")
    val name:String,
    @ColumnInfo(name = "icon")
    val icon:Int
){
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null
}