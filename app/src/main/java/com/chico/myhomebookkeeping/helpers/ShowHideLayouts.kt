package com.chico.myhomebookkeeping.helpers

import android.view.View
import android.widget.LinearLayout

class ShowHideLayouts {

    fun showHideHide(
        showFirstLayout: LinearLayout,
        hideSecondLayout: LinearLayout,
        hideThirdLayout: LinearLayout
    ) {
        showLayout(showFirstLayout)
        hideLayout(hideSecondLayout)
        hideLayout(hideThirdLayout)
    }
    fun hideLayout(element: LinearLayout) {
        element.visibility = viewGone()
    }
    fun showLayout(element: LinearLayout) {
        element.visibility = viewVisible()
    }

    private fun viewVisible(): Int {
        return View.VISIBLE
    }

    private fun viewGone(): Int {
        return View.GONE
    }


}