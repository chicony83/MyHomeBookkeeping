package com.chico.myhomebookkeeping.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R

class SettingsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val _appVersion = MutableLiveData<String>()
    val appVersion: LiveData<String>
        get() = _appVersion

    init {
        val currentVersion = app.getString(R.string.current_version)
        val packageInfo = app.packageManager.getPackageInfo(app.packageName, 0)
        @Suppress("DEPRECATION")
        val versionCode = packageInfo.versionCode
        _appVersion.value = "$currentVersion ${packageInfo.versionName} ($versionCode)"
    }
}
