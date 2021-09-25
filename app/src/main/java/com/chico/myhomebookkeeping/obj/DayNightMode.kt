package com.chico.myhomebookkeeping.obj

import com.chico.myhomebookkeeping.helpers.Message

object DayNightMode {

    private var _isNightMode = false
    val isNightMode: Boolean
        get() = _isNightMode

    fun setIsNightMode(nightMode: Boolean) {
        _isNightMode = nightMode
        Message.log("is night mode set $_isNightMode")
    }
}
