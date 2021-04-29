package com.chico.myhomebookkeeping.ui

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton

class UiHelper {
    fun showHideUIElements(selectedId: Int, showHideLayout: LinearLayout) {
        if (selectedId > 0) {
            showHideLayout.visibility = View.VISIBLE
        } else {
            showHideLayout.visibility = View.GONE
        }
    }
    fun clearUiElement(editText: EditText) {
        editText.text.clear()
    }
    fun clearUiElement(radioButton: RadioButton) {
        radioButton.isChecked = false
    }

    fun hideUiElement(element: LinearLayout) {
        element.visibility = View.GONE
    }
    fun showUiElement(element:LinearLayout){
        element.visibility = View.VISIBLE
    }
}