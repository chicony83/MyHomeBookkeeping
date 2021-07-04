package com.chico.myhomebookkeeping.helpers

import android.widget.LinearLayout
import kotlin.coroutines.coroutineContext

class UiControl(
    private val newItemLayoutHolder: LinearLayout,
    private val confirmationLayoutHolder: LinearLayout,
    private val changeItemLayoutHolder: LinearLayout
) {
//    var isConfirmationLayoutVisible = false
//    var isNewItemLayoutVisible = false
//    var isChangeLineLayoutVisible = false

    private val showHideLayouts = ShowHideLayouts()

    fun showNewItemLayoutHolder() {
        showHideLayouts.showHideHide(
            showFirstLayout = newItemLayoutHolder,
            hideSecondLayout = confirmationLayoutHolder,
            hideThirdLayout = changeItemLayoutHolder
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
    }
}