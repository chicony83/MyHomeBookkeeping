package com.chico.myhomebookkeeping.ui

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout

class UiHelper {
    fun showHideUIElements(selectedId: Int, showHideLayout: LinearLayout) {
        if (selectedId > 0) {
            showHideLayout.visibility = View.VISIBLE
        } else {
            showHideLayout.visibility = View.GONE
        }
    }
    fun clearEditText(editText: EditText) {
        editText.text.clear()
    }
    fun hideUiElement(fragment: LinearLayout) {
        fragment.visibility = View.GONE
    }
}