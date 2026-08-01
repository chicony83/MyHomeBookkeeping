package com.chico.myhomebookkeeping.db.full

import androidx.room.ColumnInfo
import com.chico.myhomebookkeeping.helpers.ParentCategoryHelper

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

    @ColumnInfo(name = "currency_short_name_value")
    val currencyShortNameValue:String?,

    @ColumnInfo(name = "currency_iso_value")
    val currencyIsoValue:String?,

    @ColumnInfo(name = "category_name_value")
    val categoryNameValue:String?,

    @ColumnInfo(name = "parent_category_name_value")
    val parentCategoryNameValue:String?,

    @ColumnInfo(name = "is_income")
    val isIncome:Boolean,

    @ColumnInfo(name = "payment_type_id")
    val paymentTypeId: Int,

    @ColumnInfo(name = "payment_type_name")
    val paymentTypeName: String,

    @ColumnInfo(name = "transfer_group_id")
    val transferGroupId: Long?,

    @ColumnInfo(name = "transfer_direction")
    val transferDirection: Int?,

    val description:String?

) {
    val categoryDisplayName: String?
        get() = ParentCategoryHelper.getCategoryDisplayName(
            parentCategoryNameValue,
            categoryNameValue
        )
}
