package com.chico.myhomebookkeeping.ui

import android.text.Editable
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
    fun isLengthStringMoThan(text: Editable):Boolean{
        if (text.length>3){
            return true
        }
        return false
    }
    fun isCheckedRadioButton(radioButton: RadioButton): Boolean {
        return radioButton.isChecked
    }

    fun isVisibleLayout(linearLayout: LinearLayout): Boolean {
        return linearLayout.visibility == View.VISIBLE
    }

    fun setShowHideOnLayout(layout: LinearLayout) {
        if (!isVisibleLayout(layout)) {
            showUiElement(layout)
        } else hideUiElement(layout)
    }
//    fun showMessage(text: String) {
//        Toast.makeText(con, text, Toast.LENGTH_LONG).show()
//    }
}