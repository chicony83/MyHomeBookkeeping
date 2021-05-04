package com.chico.myhomebookkeeping.db

import androidx.room.ColumnInfo

class FullMoneyMoving (
    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "cash_account_name_value")
    val cashAccountNameValue: String

)