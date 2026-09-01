package com.chico.myhomebookkeeping.update

import android.app.Application
import android.content.Context
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Runs data updates that must finish before any screen reads the database.
 */
object CriticalStartupUpdates {
    fun run(application: Application) {
        val preferences = application.getSharedPreferences(
            Constants.SP_NAME,
            Context.MODE_PRIVATE
        )
        val updateKey = ConstantsOfUpdate.UPDATE_72_TO_73
        val updateWasMarkedComplete = preferences.getBoolean(updateKey, false)

        runBlocking(Dispatchers.IO) {
            val hasMissingIconKeys = hasMissingIconKeys(application)
            if (!hasMissingIconKeys && updateWasMarkedComplete) return@runBlocking

            if (hasMissingIconKeys) {
                Update72To73().update(application)
            }
            check(preferences.edit().putBoolean(updateKey, true).commit()) {
                "Failed to persist completion of critical update $updateKey"
            }
        }
    }

    private fun hasMissingIconKeys(application: Application): Boolean {
        val roomDatabase = dataBase.getDataBase(application.applicationContext)
        val database = roomDatabase.openHelper.writableDatabase
        return try {
            database.query(
                "SELECT " +
                    "EXISTS(SELECT 1 FROM category_table WHERE icon_key IS NULL LIMIT 1) " +
                    "OR EXISTS(SELECT 1 FROM parent_categories_table WHERE icon_key IS NULL LIMIT 1)"
            ).use { cursor ->
                cursor.moveToFirst() && cursor.getInt(0) != 0
            }
        } finally {
            roomDatabase.close()
        }
    }
}
