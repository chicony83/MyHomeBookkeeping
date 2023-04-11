package com.chico.myhomebookkeeping.utils

import android.widget.EditText
import java.lang.StringBuilder

fun EditText.getString(): String {
    return this.text.toString()
}

fun String.hasExpression(): Boolean {
    var hasNonDigits = false

    if (this.contains('E')) return false

    this.toList().forEachIndexed { _, value ->
        if (!value.isDigit() && value != '.' && value != ',' && value != ' ') {
            hasNonDigits = true
        }
    }
    return hasNonDigits
}

fun removeWhitespacesAndCommas(number: String, decimalSeparatorSymbol: String): String {
   val a = number.replace("\\s".toRegex(), "")
    val b = when{
        decimalSeparatorSymbol == "."-> a.replace(",", "")
        decimalSeparatorSymbol == "," && a.contains(",") && a.contains(".")-> a.replace(",","")
        else -> a
    }
    return b
}