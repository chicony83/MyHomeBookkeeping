package com.chico.myhomebookkeeping.textWathers

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.chico.myhomebookkeeping.checks.CheckNewName
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class EditNameTextWatcher(
    private val namesList: List<String>,
    private val buttonList: List<Button>,
    private val errorMessageTexView: TextView,
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
                if (CheckNewName.check(s, namesList)) {
                    with(uiHelper){
                        for (i in buttonList.indices){
                            disableButton(buttonList[i])
                        }
                        Message.log("buttons disable")
                        showUiElement(errorMessageTexView)
                    }
                } else {
                    with(uiHelper){
                        for (i in buttonList.indices){
                            enableButton(buttonList[i])
                        }
                        Message.log("buttons enable")
                        hideUiElement(errorMessageTexView)

                    }
                }
            }
        }
    }
}