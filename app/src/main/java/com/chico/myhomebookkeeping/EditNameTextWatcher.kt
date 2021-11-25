package com.chico.myhomebookkeeping

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.chico.myhomebookkeeping.checks.CheckNewName
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class EditNameTextWatcher(
    private val list: List<String>,
    private val buttonList: List<Button>,
    private val errorMessage: TextView,
    private val uiHelper: UiHelper = UiHelper()
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotEmpty()) {
            launchUi {
                delay(500)
                if (CheckNewName.check(s, list)) {
                    with(uiHelper){
                        for (i in buttonList.indices){
                            disableButton(buttonList[i])
                        }
                        showUiElement(errorMessage)
                    }
                } else {
                    with(uiHelper){
                        for (i in buttonList.indices){
                            enableButton(buttonList[i])
                        }
                        hideUiElement(errorMessage)
                    }
                }
            }
        }
    }
}