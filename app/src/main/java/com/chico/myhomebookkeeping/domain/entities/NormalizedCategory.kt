package com.chico.myhomebookkeeping.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NormalizedCategory(
    val nameRes: Int? = null,
    val name: String? = null,
    val isIncome: Boolean=false,
    val iconRes: Int? = null,
    var categoriesId: Long? = null
) : Parcelable