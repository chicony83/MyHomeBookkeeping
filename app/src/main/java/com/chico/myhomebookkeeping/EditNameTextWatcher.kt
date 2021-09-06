package com.chico.myhomebookkeeping

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.chico.myhomebookkeeping.checks.CheckNewName
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class EditNameTextWatcher(
    private val namesList: List<String>,
    private val addNewCashAccountButton: Button
) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotEmpty()){
            launchUi {
                delay(500)
                CheckNewName.check(s, namesList, addNewCashAccountButton)
            }
        }
    }

}