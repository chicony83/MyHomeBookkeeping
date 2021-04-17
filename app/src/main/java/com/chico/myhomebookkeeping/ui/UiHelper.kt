package com.chico.myhomebookkeeping.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.NavController
import com.chico.myhomebookkeeping.R

class UiHelper {
    fun showHideUIElements(selectedId: Int, showHideLayout: LinearLayout) {
        if (selectedId > 0) {
            showHideLayout.visibility = View.VISIBLE
        } else {
            showHideLayout.visibility = View.GONE
        }
    }

}