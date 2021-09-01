package com.chico.myhomebookkeeping.helpers

import android.view.View
import android.widget.LinearLayout

class ShowHideLayouts {
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
}