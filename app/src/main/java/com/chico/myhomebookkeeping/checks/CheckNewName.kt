package com.chico.myhomebookkeeping.checks

import android.text.Editable
import android.widget.Button
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

object CheckNewName {
    fun check(s: Editable?, namesList: List<String>, addNewCashAccountButton: Button) {
        Message.log("ищем")
        search@ for (i in namesList.indices) {
            if (s.toString().equals(namesList[i], ignoreCase = true)) {
//                    showMessage("найдено совпадение $s == ${namesList[i]}")
                addNewCashAccountButton.isEnabled = false
                break@search
            } else {
                Message.log("$s != ${namesList[i]}")
                addNewCashAccountButton.isEnabled = true
            }

        }
    }
}