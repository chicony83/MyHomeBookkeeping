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
        search@ for (i in namesList.indices) {
            if (s.toString().equals(namesList[i], ignoreCase = true)) {
//                    showMessage("найдено совпадение $s == ${namesList[i]}")
                return true
                break@search
            } else {
                Message.log("$s != ${namesList[i]}")
                return false
            }
        }
        return false
    }
}