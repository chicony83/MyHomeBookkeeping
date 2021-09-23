package com.chico.myhomebookkeeping.checks

import android.content.Context
import android.content.res.Configuration

class CheckNightMode {
    fun isNightMode(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                false
            }
            else -> {
                true
            }
        }
    }
}