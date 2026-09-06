package com.chico.myhomebookkeeping.ui.firstLaunch

import android.content.Context
import android.content.SharedPreferences
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants

object FirstLaunchStateManager {
    private const val DATABASE_NAME = "DataBase"
    private var launchWasPreparedInThisProcess = false

    fun prepareForLaunch(context: Context, isColdStart: Boolean): FirstLaunchState {
        val preferences = preferences(context)
        val state = readOrMigrateState(context, preferences)
        val isFirstActivityInThisProcess = !launchWasPreparedInThisProcess
        launchWasPreparedInThisProcess = true

        if ((isColdStart || isFirstActivityInThisProcess) &&
            state == FirstLaunchState.CONFIGURING
        ) {
            val databaseFile = context.getDatabasePath(DATABASE_NAME)
            if (databaseFile.exists()) {
                check(context.deleteDatabase(DATABASE_NAME)) {
                    "Failed to clear database after interrupted first launch"
                }
            }
            clearInterruptedSetupPreferences(preferences)
            Message.log("---interrupted first launch reset---")
        }

        Message.log("---first launch state = $state---")
        return state
    }

    fun shouldOpenSetup(context: Context): Boolean {
        val preferences = preferences(context)
        return when (readOrMigrateState(context, preferences)) {
            FirstLaunchState.FIRST_START -> {
                check(
                    preferences.edit()
                        .putString(
                            Constants.FIRST_LAUNCH_STATE,
                            FirstLaunchState.CONFIGURING.name
                        )
                        .putBoolean(Constants.IS_FIRST_LAUNCH, true)
                        .commit()
                ) { "Failed to persist first launch configuration state" }
                Message.log("---first launch state = ${FirstLaunchState.CONFIGURING}---")
                true
            }
            FirstLaunchState.CONFIGURING -> true
            FirstLaunchState.COMPLETED -> false
        }
    }

    fun completeSetup(context: Context) {
        check(
            preferences(context).edit()
                .putString(Constants.FIRST_LAUNCH_STATE, FirstLaunchState.COMPLETED.name)
                // Compatibility with older app versions. This flag can be removed in a future
                // release after users have had enough time to migrate to FIRST_LAUNCH_STATE.
                .putBoolean(Constants.IS_FIRST_LAUNCH, false)
                .commit()
        ) { "Failed to persist completed first launch state" }
        Message.log("---first launch state = ${FirstLaunchState.COMPLETED}---")
    }

    fun getState(context: Context): FirstLaunchState =
        readOrMigrateState(context, preferences(context))

    private fun readOrMigrateState(
        context: Context,
        preferences: SharedPreferences
    ): FirstLaunchState {
        preferences.getString(Constants.FIRST_LAUNCH_STATE, null)?.let { savedState ->
            FirstLaunchState.values().firstOrNull { it.name == savedState }?.let { return it }
        }

        val migratedState = when {
            // IS_FIRST_LAUNCH is kept only for migration from older releases. Its compatibility
            // handling can be removed in a future version after the migration window has passed.
            preferences.contains(Constants.IS_FIRST_LAUNCH) -> {
                if (preferences.getBoolean(Constants.IS_FIRST_LAUNCH, true)) {
                    FirstLaunchState.CONFIGURING
                } else {
                    FirstLaunchState.COMPLETED
                }
            }
            context.getDatabasePath(DATABASE_NAME).exists() -> FirstLaunchState.COMPLETED
            else -> FirstLaunchState.FIRST_START
        }

        check(
            preferences.edit()
                .putString(Constants.FIRST_LAUNCH_STATE, migratedState.name)
                .commit()
        ) { "Failed to migrate first launch state" }
        return migratedState
    }

    private fun clearInterruptedSetupPreferences(preferences: SharedPreferences) {
        check(
            preferences.edit()
                .remove(Constants.FIRST_LAUNCH_INSTALL_MODE)
                .remove(Constants.CLEAN_INSTALL_MESSAGE_PENDING)
                .remove(Constants.START_FRAGMENT)
                .remove(Constants.CATEGORIES_DISPLAY_MODE)
                .commit()
        ) { "Failed to clear interrupted first launch preferences" }
    }

    private fun preferences(context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
}
