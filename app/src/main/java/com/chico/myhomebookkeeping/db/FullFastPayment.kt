package com.chico.myhomebookkeeping.db

import androidx.room.ColumnInfo

class FullFastPayment(

    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "icon")
    val icon: Int?,

    @ColumnInfo(name = "rating")
    val rating: Int,

    @ColumnInfo(name = "cash_account")
    val cashAccountNameValue: String,

    @ColumnInfo(name = "currency")
    val currencyNameValue:String,

    @ColumnInfo(name = "category")
    val categoryNameValue:String,

    @ColumnInfo(name = "amount")
    val amount: Double?,

    @ColumnInfo(name = "description")
    val description: String?
)


