package com.chico.myhomebookkeeping.checks

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.chico.myhomebookkeeping.R
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class GetVersionCode(
    private val activity: Activity,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    fun getNewVersion(onCheckFinished: () -> Unit) {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                val updateType = when {
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                    else -> null
                }
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.message_you_can_update_the_program),
                        Toast.LENGTH_SHORT
                    ).show()
                    if (updateType == null || !startUpdateFlow(appUpdateInfo, updateType)) {
                        onCheckFinished()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.message_you_have_latest_version),
                        Toast.LENGTH_SHORT
                    ).show()
                    onCheckFinished()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.message_update_check_failed),
                    Toast.LENGTH_SHORT
                ).show()
                onCheckFinished()
            }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int): Boolean {
        return runCatching {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(updateType).build()
            )
        }.getOrElse {
            Toast.makeText(
                activity,
                activity.getString(R.string.message_update_check_failed),
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    fun completeDownloadedUpdateIfNeeded() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }
}
