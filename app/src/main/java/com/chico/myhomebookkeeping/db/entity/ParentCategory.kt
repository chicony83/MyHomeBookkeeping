package com.chico.myhomebookkeeping.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "parent_categories_table")
data class ParentCategory(
    @ColumnInfo(name = "name_res")
    val nameRes: Int,
    @ColumnInfo(name = "is_income")
    val isIncome: Boolean,
    @ColumnInfo(name = "icon_res")
    val iconRes: Int?
):Parcelable
{
    @PrimaryKey(autoGenerate = true)
    var categoriesId: Long? = null
}