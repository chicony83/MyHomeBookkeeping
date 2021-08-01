package com.chico.myhomebookkeeping.checks

import android.text.Editable

object UiElementsCheck {
    fun isEntered(text: Editable):Boolean{
        return text.isNotEmpty()
    }
}