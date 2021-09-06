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
        button: Button,
        errorMessage: TextView
    ) {
        Message.log("ищем")
        search@ for (i in namesList.indices) {
            if (s.toString().equals(namesList[i], ignoreCase = true)) {
//                    showMessage("найдено совпадение $s == ${namesList[i]}")
                button.isEnabled = false
                errorMessage.visibility = View.VISIBLE
                break@search
            } else {
                Message.log("$s != ${namesList[i]}")
                button.isEnabled = true
                errorMessage.visibility = View.GONE
            }

        }
    }
}