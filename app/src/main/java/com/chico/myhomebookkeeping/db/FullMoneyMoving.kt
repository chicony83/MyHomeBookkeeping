package com.chico.myhomebookkeeping.db

import androidx.room.ColumnInfo

class FullMoneyMoving (
    @ColumnInfo(name = "id")
    val id:Long,

    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "cash_account_name_value")
    val cashAccountNameValue: String,

    @ColumnInfo(name = "currency_name_value")
    val currencyNameValue:String,

    @ColumnInfo(name = "category_name_value")
    val categoryNameValue:String,

    @ColumnInfo(name = "is_income")
    val isIncome:Boolean

)