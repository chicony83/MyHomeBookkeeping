package com.chico.myhomebookkeeping.interfaces

import com.chico.myhomebookkeeping.db.full.FullFastPayment

interface OnItemSelectForChangeCallBackLong {
    fun onSelect(fastPayment: FullFastPayment)
}