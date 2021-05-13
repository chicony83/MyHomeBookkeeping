package com.chico.myhomebookkeeping.ui

import android.content.Context
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.chico.myhomebookkeeping.constants.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UiHelper(
    val minLength: Int = Constants.MIN_LENGTH_NAME
) {
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

    fun showUiElement(element: LinearLayout) {
        element.visibility = View.VISIBLE
    }

    fun isLengthStringMoThan(text: Editable): Boolean {
        if (text.length > minLength) {
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

    fun hideUiElement(fab: FloatingActionButton) {
        fab.visibility = View.GONE
    }

    fun showUiElement(fab: FloatingActionButton) {
        fab.visibility = View.VISIBLE
    }

    fun clearUiListRadioButton(listOf: List<RadioButton>) {
        for (element in listOf){
            clearUiElement(element)
        }
    }

    fun clearUiListEditText(listOf: List<EditText>) {
        for (element in listOf){
            clearUiElement(element)
        }
    }

    fun hideUiElement(button: Button) {
        button.visibility = View.GONE
    }
}