package com.chico.myhomebookkeeping.helpers

import android.content.res.ColorStateList
import android.os.Build
import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Colors
import com.chico.myhomebookkeeping.obj.DayNightMode

class UiColors {
    fun setColors(
        buttonsList: List<Button>,
        buttonsListForColorButtonText: List<Button>
    ) {
        setButtonsBackgroundColor(buttonsList = buttonsList, color = Colors.dialogButtonsColor)
        setButtonsTextColor(buttonsListForColorButtonText)
    }

    fun setColors(
        dialogsList: List<ViewBinding>,
        buttonsList: List<Button>,
        buttonsListForColorButtonText: List<Button>
    ) {
        setDialogBackgroundColor(dialogsList = dialogsList, color = Colors.dialogBackgroundColor)
        setButtonsBackgroundColor(buttonsList = buttonsList, color = Colors.dialogButtonsColor)
        setButtonsTextColor(buttonsListForColorButtonText)
    }

    private fun setButtonsTextColor(buttonsList: List<Button>) {
        when (DayNightMode.isNightMode) {
            true -> {
//                message("ночь")
            }
            false -> {
//                message("день")
                setColorTextOnButton(
                    buttonsList,
                    Colors.dialogButtonsTextColor
                )
            }
        }
    }

    private fun setButtonsBackgroundColor(buttonsList: List<Button>, color: Int) {
        for (item in buttonsList) {
            item.setBackgroundResource(color)
        }
    }

    private fun setDialogBackgroundColor(dialogsList: List<ViewBinding>, color: Int) {
        for (item in dialogsList) {
            item.root.setBackgroundResource(color)
        }
    }

    private fun setColorTextOnButton(list: List<Button>, color: Int) {
        for (item in list) {
            item.setTextColor(color)
        }
    }

    private fun setButtonsBackgroundTintColor(buttonsList: List<Button>, color: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (item in buttonsList) {
                item.backgroundTintList = color
            }

        }
    }
}