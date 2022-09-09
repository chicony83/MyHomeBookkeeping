package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class Currencies(
    @ColumnInfo(name = "currency_name")
    val currencyName: String,
    @ColumnInfo(name = "is_currency_default")
    val isCurrencyDefault: Boolean,
    @ColumnInfo(name = "icon_currency")
    val icon: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var currencyId: Int? = null
}
