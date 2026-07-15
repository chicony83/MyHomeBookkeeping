package com.chico.myhomebookkeeping.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_type_table")
data class PaymentType(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "payment_type_code")
    val code: String,
    @ColumnInfo(name = "payment_type_name")
    val name: String,
    @ColumnInfo(name = "affects_reports")
    val affectsReports: Boolean,
    @ColumnInfo(name = "requires_category")
    val requiresCategory: Boolean,
    @ColumnInfo(name = "requires_second_account")
    val requiresSecondAccount: Boolean
)
