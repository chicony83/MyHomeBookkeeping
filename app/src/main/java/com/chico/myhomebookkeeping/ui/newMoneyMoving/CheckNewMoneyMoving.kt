package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.text.Editable

object CheckNewMoneyMoving {
    fun isEntered(text: Editable):Boolean{
        return text.isNotEmpty()
    }
}