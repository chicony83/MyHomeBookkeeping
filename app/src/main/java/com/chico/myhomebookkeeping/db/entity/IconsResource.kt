package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_resource_table")
data class IconsResource(
    @ColumnInfo(name = "icon_name")
    val iconName:String,

    @ColumnInfo(name = "icon_category")
    val iconCategory:Int?,

    @ColumnInfo(name = "icon_resource")
    val iconResources:Int
) {
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null

}