package com.chico.myhomebookkeeping.utils

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.isInvisible

fun smoothVisibleInvisible(view: View, show: Boolean) {
    if (show) {
        view.alpha = 0f
        view.animate().apply {
            interpolator = LinearInterpolator()
            duration = 200
            alpha(1f)
            start()
        }
    } else {
        view.alpha = 1f
        view.animate().apply {
            interpolator = LinearInterpolator()
            duration = 200
            alpha(0f)
            start()
        }
    }
    view.isInvisible = !show
}

fun visibleInvisible(view: View, show: Boolean) {
    view.isInvisible = !show
}