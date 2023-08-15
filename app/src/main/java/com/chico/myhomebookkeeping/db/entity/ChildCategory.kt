package com.chico.myhomebookkeeping.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "child_categories_table")
@Parcelize
data class ChildCategory(
    @ColumnInfo(name = "name_res")
    val nameRes: Int? = null,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "icon_res")
    val iconRes: Int? = null,
    @ColumnInfo(name = "parent_category_name_res")
    val parentNameRes: Int? = null,
    @ColumnInfo(name = "parent_category_name")
    val parentName: String? = null
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}