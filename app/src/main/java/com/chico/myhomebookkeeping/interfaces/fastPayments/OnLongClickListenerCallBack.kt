package com.chico.myhomebookkeeping.interfaces.fastPayments

import android.view.View
import com.chico.myhomebookkeeping.db.full.FullFastPayment

interface OnLongClickListenerCallBack:View.OnLongClickListener {
    fun longClick(fullFastPayment: FullFastPayment): Boolean
}