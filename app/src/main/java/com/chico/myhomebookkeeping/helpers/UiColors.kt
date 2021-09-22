package com.chico.myhomebookkeeping.helpers

import android.content.res.ColorStateList
import android.os.Build
import android.widget.Button
import androidx.viewbinding.ViewBinding

class UiColors {

    fun setButtonsBackgroundColor(buttonsList: List<Button>, color: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (item in buttonsList) {
                item.backgroundTintList = color
            }
        }
    }

    fun setDialogBackgroundColor(dialogsList: List<ViewBinding>, resource: Int) {
        for (item in dialogsList){
            item.root.setBackgroundResource(resource)
        }
    }

    fun setColorTextOnButton(list: List<Button>, color: Int) {
        for (item in list){
            item.setTextColor(color)
        }
    }
}