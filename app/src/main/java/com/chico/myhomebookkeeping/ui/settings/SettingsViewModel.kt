package com.chico.myhomebookkeeping.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.BuildConfig
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP

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

    private val _appVersion = MutableLiveData<String>()
    val appVersion: LiveData<String>
        get() = _appVersion

    init {
        val currentVersion = app.getString(R.string.current_version)
        _appVersion.postValue("$currentVersion ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
    }
}