package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icons_resources_table")
data class IconsResource(
    @ColumnInfo(name = "icon_category")
    val iconCategory:Int?,

    @ColumnInfo(name = "icon_resources")
    val iconResources:Int
) {
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null

}