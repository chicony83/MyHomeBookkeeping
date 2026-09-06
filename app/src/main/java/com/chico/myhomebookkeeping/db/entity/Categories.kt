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
    val parentCategoryId:Int?,
    @ColumnInfo(name = "category_order")
    val categoryOrder: Int = 0,
    @ColumnInfo(name = "category_name_ru")
    val categoryNameRu: String? = null,
    @ColumnInfo(name = "category_name_pl")
    val categoryNamePl: String? = null,
    @ColumnInfo(name = "category_name_de")
    val categoryNameDe: String? = null,
    @ColumnInfo(name = "category_name_be")
    val categoryNameBe: String? = null,
    @ColumnInfo(name = "category_name_be_latn")
    val categoryNameBeLatn: String? = null,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,
    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,
    @ColumnInfo(name = "hidden_at")
    val hiddenAt: Long? = null,
    @ColumnInfo(name = "icon_key")
    val iconKey: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var categoriesId: Int? = null
}
