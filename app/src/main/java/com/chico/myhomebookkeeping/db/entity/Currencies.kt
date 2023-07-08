package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class Currencies(

    @ColumnInfo(name = "currency_name")
    val currencyName: String,

    @ColumnInfo(name = "currency_name_short")
    val currencyNameShort:String?=null,

    @ColumnInfo(name = "iso_4217")
    val iso4217:String?=null,

    @ColumnInfo(name = "icon_currency")
    val icon: Int?=-1,

    @ColumnInfo(name = "is_currency_default")
    var isCurrencyDefault: Boolean?=null

    ) {

    @PrimaryKey(autoGenerate = true)
    var currencyId: Int? = null
}