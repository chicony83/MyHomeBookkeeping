package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class Currency(
    @ColumnInfo(name = "currency_long_name")
    val currencyLongName: String,
    @ColumnInfo(name = "currency_short_name")
    val currencyShortName: String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}
