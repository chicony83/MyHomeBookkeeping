package com.chico.myhomebookkeeping.interfaces

import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.full.FullFastPayment

interface OnCurrencyClickListener {
    fun onShortClick(currency: Currencies)
    fun onLongClick(currency: Currencies)
}