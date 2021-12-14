package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icons_table")
class Icons(
    @ColumnInfo(name = "name")
    val name:String,
    @ColumnInfo(name = "icon_value")
    val iconValue: String
) {
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null
}