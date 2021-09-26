package com.chico.myhomebookkeeping.checks

import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chico.myhomebookkeeping.helpers.Message

object CheckNewName {
    fun check(
        s: Editable?,
        namesList: List<String>,
    ): Boolean {
        Message.log("ищем")
        for (i in namesList.indices) {
            return if (s.toString().equals(namesList[i], ignoreCase = true)) {
                //                    showMessage("найдено совпадение $s == ${namesList[i]}")
                true
            } else {
                Message.log("$s != ${namesList[i]}")
                false
            }
        }
        return false
    }
}