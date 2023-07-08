package com.chico.myhomebookkeeping.interfaces

import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.domain.entities.NormalizedCategory

interface OnCategoryClickListener {
    fun onShortClick(category: NormalizedCategory)
    fun onLongClick(category: NormalizedCategory)
}