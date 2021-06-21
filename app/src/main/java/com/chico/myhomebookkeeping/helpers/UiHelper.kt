package com.chico.myhomebookkeeping.helpers

import android.text.Editable
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.chico.myhomebookkeeping.constants.Constants

import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UiHelper(

) {
    val minLength: Int = Constants.MIN_LENGTH_NAME

    fun clearUiElement(editText: EditText) {
        editText.text.clear()
    }

    fun clearUiElement(radioButton: RadioButton) {
        radioButton.isChecked = false
    }

    fun hideUiElement(element: LinearLayout) {
        element.visibility = viewGone()
    }

    fun showUiElement(element: LinearLayout) {
        element.visibility = viewVisible()
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
        return linearLayout.visibility == viewVisible()
    }

    fun setShowHideOnLayout(layout: LinearLayout) {
        if (!isVisibleLayout(layout)) {
            showUiElement(layout)
        } else hideUiElement(layout)
    }

    fun hideUiElement(fab: FloatingActionButton) {
        fab.visibility = viewGone()
    }

    fun showUiElement(fab: FloatingActionButton) {
        fab.visibility = viewVisible()
    }

    fun clearUiListRadioButton(listOf: List<RadioButton>) {
        for (element in listOf) {
            clearUiElement(element)
        }
    }

    fun clearUiListEditText(listOf: List<EditText>) {
        for (element in listOf) {
            clearUiElement(element)
        }
    }

    fun hideUiElement(button: Button) {
        button.visibility = viewGone()
    }

    fun hideUiElement(textView: TextView) {
        textView.visibility = viewGone()
    }

    fun showUiElement(textView: TextView) {
        textView.visibility = viewVisible()
    }

    fun hideUiElement(element: ConstraintLayout) {
        element.visibility = viewGone()
    }

    fun unblockButton(button: MaterialButton) {
        button.isEnabled = true
    }

    fun blockButton(button: MaterialButton) {
        button.isEnabled = false
    }

    fun showHideUIElements(id: Int, layout: View) {
        if (id > 0) {
            showLayout(layout)
        } else {
            hideLayout(layout)
        }
    }

    private fun hideLayout(layout: View) {
        layout.visibility = viewGone()
    }

    private fun showLayout(layout: View) {
        layout.visibility = viewVisible()
    }

    private fun viewVisible(): Int {
        return View.VISIBLE
    }

    private fun viewGone(): Int {
        return View.GONE
    }
}