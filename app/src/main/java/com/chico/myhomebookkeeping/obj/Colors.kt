package com.chico.myhomebookkeeping.obj

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import com.chico.myhomebookkeeping.R

object Colors {
    private lateinit var _resources: Resources

    private var _dialogBackgroundColor = 0
    val dialogBackgroundColor: Int
        get() = _dialogBackgroundColor

    private lateinit var _dialogButtonsColor: ColorStateList
    val dialogButtonsColor: ColorStateList
        get() = _dialogButtonsColor

    private var _dialogButtonsTextColor = 0
    val dialogButtonsTextColor: Int
        get() = _dialogButtonsTextColor

    fun setColors(resources: Resources) {
        _resources = resources
        when (DayNightMode.isNightMode) {
            true -> {
                _dialogBackgroundColor = R.drawable.dialog_background_night
                _dialogButtonsColor = getNightColorForButtonsBackground()
            }
            false -> {
                _dialogButtonsTextColor = R.color.buttonsTextColorDay
                _dialogBackgroundColor = R.drawable.dialog_background_day
                _dialogButtonsColor = getDayColorForButtonsBackground()
            }
        }
    }

    private fun getDayColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonDayBackground)
    }

    private fun getNightColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonNightBackground)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun getButtonsBackgroundColor(color: Int): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            _resources.getColorStateList(color, null)
        } else {
            _resources.getColorStateList(color)
        }
    }

}