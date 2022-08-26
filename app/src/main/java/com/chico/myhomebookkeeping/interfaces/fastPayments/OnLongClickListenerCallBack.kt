package com.chico.myhomebookkeeping.interfaces.fastPayments

import android.view.View

interface OnLongClickListenerCallBack:View.OnLongClickListener {
    fun longClick(long:Long): Boolean
}