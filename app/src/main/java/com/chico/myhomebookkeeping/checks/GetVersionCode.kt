package com.chico.myhomebookkeeping.checks

import android.content.Context
import android.widget.Toast
import com.chico.myhomebookkeeping.R
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.UpdateAvailability


class GetVersionCode(val context: Context) {
    fun getNewVersion() {

//        val appUpdateManager = AppUpdateManagerFactory.create(context)
//
//        // Returns an intent object that you use to check for an update.
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//        var message = ""
//        // Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                // This example applies an immediate update. To apply a flexible update
//                // instead, pass in AppUpdateType.FLEXIBLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
//            ) {
//                message = context.getString(R.string.message_you_can_update_the_program)
//            } else {
//                message = context.getString(R.string.message_you_have_latest_version)
//            }
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        }
    }
}
