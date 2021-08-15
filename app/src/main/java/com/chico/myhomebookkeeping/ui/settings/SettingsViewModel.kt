package com.chico.myhomebookkeeping.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.helpers.SetSP

class SettingsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences = app.getSharedPreferences(
        spName,
        Context.MODE_PRIVATE
    )
    private val spEditor = sharedPreferences.edit()
    private val saveARGS = SetSP(spEditor)

}