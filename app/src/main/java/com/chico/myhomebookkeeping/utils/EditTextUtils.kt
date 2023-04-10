package com.chico.myhomebookkeeping.utils

import android.widget.EditText

fun EditText.getString():String{
    return this.text.toString()
}

fun String.hasExpression(): Boolean {
    var hasNonDigits = false

    if (this.contains('E')) return false

    this.toList().forEachIndexed { _, value->
        if (!value.isDigit()&&value!='.'&&value!=',') {
            hasNonDigits = true
        }
    }
    return hasNonDigits
}

fun String.removeWhitespaces() = replace("\\s".toRegex(), "")