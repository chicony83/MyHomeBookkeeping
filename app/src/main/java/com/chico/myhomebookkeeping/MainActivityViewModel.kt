package com.chico.myhomebookkeeping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.checks.AppVersion
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.sp.GetSP

class MainActivityViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private var sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private var getSP = GetSP(sharedPreferences)

    init {
        initializeVersionTrackingForNewInstallation()
    }

    private fun initializeVersionTrackingForNewInstallation() {
        val isNewInstallation = !sharedPreferences.contains(Constants.IS_FIRST_LAUNCH)
        val hasTrackedVersion = sharedPreferences.contains(ConstantsOfUpdate.LAST_CHECKED_VERSION)
        if (isNewInstallation && !hasTrackedVersion) {
            sharedPreferences.edit()
                .putInt(ConstantsOfUpdate.LAST_CHECKED_VERSION, AppVersion.code(app))
                .apply()
        }
    }

    fun checkIsFirstLaunch(): Boolean {
        Message.log("---is first launch = ${getSP.getBooleanElseReturnTrue(Constants.IS_FIRST_LAUNCH)}")
        return getSP.getBooleanElseReturnTrue(Constants.IS_FIRST_LAUNCH)
    }

    fun setFirstLaunchFlag(flag: Boolean) {
        getSP.setBoolean(Constants.IS_FIRST_LAUNCH, flag)
    }

}
