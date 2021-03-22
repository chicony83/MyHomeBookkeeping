package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class Currencies(
    @ColumnInfo(name = "currency_name")
    val currencyName: String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}
