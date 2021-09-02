package com.chico.myhomebookkeeping.helpers

import android.widget.Button
import android.widget.LinearLayout

class UiControl(
    private val topButtonsHolder: LinearLayout,
    private val bottomButton: Button,
    private val newItemLayoutHolder: LinearLayout,
    private val confirmationLayoutHolder: LinearLayout,
    private val changeItemLayoutHolder: LinearLayout
) {
    private val uiHelper = UiHelper()
    private val showHideLayouts = ShowHideDialogsController()

    fun showNewItemLayoutHolder() {
        showHideLayouts.showHideHide(
            showFirstLayout = newItemLayoutHolder,
            hideSecondLayout = confirmationLayoutHolder,
            hideThirdLayout = changeItemLayoutHolder
        )
        showHideLayouts.hideUIControlElements(
            topButtonsHolder = topButtonsHolder,
            bottomButton = bottomButton
        )
    }
    fun showSelectLayoutHolder(){
        showHideLayouts.showHideHide(
            showFirstLayout = confirmationLayoutHolder ,
            hideSecondLayout = newItemLayoutHolder,
            hideThirdLayout = changeItemLayoutHolder
        )
    }
    fun showChangeLayoutHolder(){
        showHideLayouts.showHideHide(
            showFirstLayout = changeItemLayoutHolder,
            hideSecondLayout = newItemLayoutHolder,
            hideThirdLayout = confirmationLayoutHolder
        )
        showHideLayouts.hideUIControlElements(
            topButtonsHolder = topButtonsHolder,
            bottomButton = bottomButton
        )

    }
}