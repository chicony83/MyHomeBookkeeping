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
//        Message.log("ищем")
//        Message.log("names list size = ${namesList.size}")
        for (i in 0 until namesList.size) {
//            Message.log("проверяется строка $i , ")
            if (s.toString().equals(namesList[i], ignoreCase = true)) {
//                Message.log("найдено совпадение $s == ${namesList[i]} !!! ")
                return true
            }
        }
//        Message.log("совпадений не найдено")
        return false
    }
}