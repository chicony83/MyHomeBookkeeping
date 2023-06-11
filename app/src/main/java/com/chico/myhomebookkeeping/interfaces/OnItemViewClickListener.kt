package com.chico.myhomebookkeeping.interfaces

import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.full.FullFastPayment

interface OnItemViewClickListenerLong {
    fun onClick(fullFastPayment: FullFastPayment, childCategory: ChildCategory)
}

interface OnItemViewClickListener {
    fun onShortClick(selectedId: Int)
    fun onLongClick(selectedId: Int)
}