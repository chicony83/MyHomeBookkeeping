package com.chico.myhomebookkeeping

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.chico.myhomebookkeeping.checks.CheckNewName
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class EditNameTextWatcher(
    private val list: List<String>,
    private val button: Button,
    private val errorMessage: TextView
) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotEmpty()) {
            launchUi {
                delay(500)
                CheckNewName.check(s, list, button, errorMessage)
            }
        }
    }

}