package com.chico.myhomebookkeeping.helpers

import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class ShowHideDialogsController {
    private val uiHelper = UiHelper()

    fun showHideHide(
        showFirstLayout: LinearLayout,
        hideSecondLayout: LinearLayout,
        hideThirdLayout: LinearLayout
    ) {
        uiHelper.showUiElement(showFirstLayout)
        uiHelper.hideUiElement(hideSecondLayout)
        uiHelper.hideUiElement(hideThirdLayout)
    }

    fun hideUIControlElements(topButtonsHolder: LinearLayout, bottomButton: Button) {
        uiHelper.hideUiElement(topButtonsHolder)
        uiHelper.hideUiElement(bottomButton)
    }
    fun showUIControlElements(topButtonsHolder: LinearLayout, bottomButton: Button){
        uiHelper.showUiElement(topButtonsHolder)
        uiHelper.showUiElement(bottomButton)
    }
}