package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fast_payments_table")
data class FastPayments(
    @ColumnInfo(name = "icon")
    val icon: Int?,

    @ColumnInfo(name = "about_fast_payment")
    val aboutFastPayment:String,

    @ColumnInfo(name = "rating")
    val rating:Int,

    @ColumnInfo(name = "cash_account")
    val cashAccountId: Int,

    @ColumnInfo(name = "currency")
    val currencyId: Int,

    @ColumnInfo(name = "category")
    val categoryId: Int,

    @ColumnInfo(name = "amount")
    val amount:Double?,

    @ColumnInfo(name = "description")
    val description: String?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
