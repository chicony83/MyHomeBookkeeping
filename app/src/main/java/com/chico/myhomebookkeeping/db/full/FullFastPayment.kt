package com.chico.myhomebookkeeping.db.full

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FullFastPayment(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "icon")
    val icon: Int? = null,

    @ColumnInfo(name = "name_fast_payment")
    val nameFastPayment: String,

    @ColumnInfo(name = "rating")
    val rating: Int,

    @ColumnInfo(name = "cash_account_name_value")
    val cashAccountNameValue: String,

    @ColumnInfo(name = "currency_name_value")
    val currencyNameValue: String,

    @ColumnInfo(name = "category_name_value")
    val categoryNameValue: String,

    @ColumnInfo(name = "icon_resource_value")
    val categoryResourceValue: Int? = null,

    @ColumnInfo(name = "is_income")
    val isIncome: Boolean,

    @ColumnInfo(name = "amount")
    val amount: Double? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "is_user_custom")
    val isUserCustom: Boolean = false,

    @ColumnInfo(name = "childCategories")
    val childCategories: List<ChildCategory>
):Parcelable


