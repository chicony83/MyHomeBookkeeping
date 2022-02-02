package com.chico.myhomebookkeeping.db.full

import androidx.room.ColumnInfo

class FullFastPayment(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "icon")
    val icon: Int?,

    @ColumnInfo(name = "name_fast_payment")
    val nameFastPayment:String,

    @ColumnInfo(name = "rating")
    val rating: Int,

    @ColumnInfo(name = "cash_account_name_value")
    val cashAccountNameValue: String,

    @ColumnInfo(name = "currency_name_value")
    val currencyNameValue:String,

    @ColumnInfo(name = "category_name_value")
    val categoryNameValue:String,

    @ColumnInfo(name = "is_income")
    val isIncome:Boolean,

    @ColumnInfo(name = "amount")
    val amount: Double?,

    @ColumnInfo(name = "description")
    val description: String?
)


