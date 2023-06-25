package com.chico.myhomebookkeeping.textWathers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchUi

class NewMoneyMovingAmountTextWatcher(
    private val eraseButton: ImageButton
) : TextWatcher {

    val uiHelper = UiHelper()
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        if (p0.toString().isNotEmpty()) {
            launchUi {
                uiHelper.showUiElement(eraseButton)
            }
        } else {
            launchUi { uiHelper.hideUiElement(eraseButton) }
        }
    }
}