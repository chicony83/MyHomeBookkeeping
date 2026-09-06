package com.chico.myhomebookkeeping

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.firstLaunch.FirstLaunchInstallMode
import com.chico.myhomebookkeeping.ui.firstLaunch.FirstLaunchState
import com.chico.myhomebookkeeping.ui.firstLaunch.FirstLaunchStateManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirstLaunchStateManagerInstrumentedTest {
    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }
    private val preferences by lazy {
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    }

    @Before
    fun resetAppState() {
        context.deleteDatabase(DATABASE_NAME)
        preferences.edit().clear().commit()
    }

    @After
    fun cleanUpAppState() {
        context.deleteDatabase(DATABASE_NAME)
        preferences.edit().clear().commit()
    }

    @Test
    fun newInstallationStartsConfiguration() {
        assertEquals(
            FirstLaunchState.FIRST_START,
            FirstLaunchStateManager.prepareForLaunch(context, isColdStart = true)
        )

        assertTrue(FirstLaunchStateManager.shouldOpenSetup(context))
        assertEquals(FirstLaunchState.CONFIGURING, FirstLaunchStateManager.getState(context))
        assertTrue(preferences.getBoolean(Constants.IS_FIRST_LAUNCH, false))
    }

    @Test
    fun interruptedConfigurationClearsDatabaseAndTemporarySetupPreferences() = runBlocking {
        preferences.edit()
            .putString(Constants.FIRST_LAUNCH_STATE, FirstLaunchState.CONFIGURING.name)
            .putString(Constants.FIRST_LAUNCH_INSTALL_MODE, FirstLaunchInstallMode.CUSTOM.name)
            .putString(Constants.START_FRAGMENT, Constants.START_FRAGMENT_JOURNAL)
            .putString(Constants.APP_LANGUAGE, Constants.APP_LANGUAGE_RUSSIAN)
            .commit()
        addCurrencyToDatabase()

        FirstLaunchStateManager.prepareForLaunch(context, isColdStart = true)

        assertFalse(context.getDatabasePath(DATABASE_NAME).exists())
        assertFalse(preferences.contains(Constants.FIRST_LAUNCH_INSTALL_MODE))
        assertFalse(preferences.contains(Constants.START_FRAGMENT))
        assertEquals(
            Constants.APP_LANGUAGE_RUSSIAN,
            preferences.getString(Constants.APP_LANGUAGE, null)
        )
        assertEquals(FirstLaunchState.CONFIGURING, FirstLaunchStateManager.getState(context))
    }

    @Test
    fun activityRecreationDoesNotClearConfigurationDatabase() = runBlocking {
        FirstLaunchStateManager.prepareForLaunch(context, isColdStart = false)
        preferences.edit()
            .putString(Constants.FIRST_LAUNCH_STATE, FirstLaunchState.CONFIGURING.name)
            .commit()
        addCurrencyToDatabase()

        FirstLaunchStateManager.prepareForLaunch(context, isColdStart = false)

        val database = dataBase.getDataBase(context)
        try {
            assertEquals(1, database.currenciesDao().getCurrenciesCount())
        } finally {
            database.close()
        }
    }

    @Test
    fun completedLegacyInstallationIsMigratedWithoutDeletingDatabase() = runBlocking {
        preferences.edit().putBoolean(Constants.IS_FIRST_LAUNCH, false).commit()
        addCurrencyToDatabase()

        assertEquals(
            FirstLaunchState.COMPLETED,
            FirstLaunchStateManager.prepareForLaunch(context, isColdStart = true)
        )
        assertFalse(FirstLaunchStateManager.shouldOpenSetup(context))

        val database = dataBase.getDataBase(context)
        try {
            assertEquals(1, database.currenciesDao().getCurrenciesCount())
        } finally {
            database.close()
        }
    }

    @Test
    fun interruptedLegacyInstallationIsReset() = runBlocking {
        preferences.edit().putBoolean(Constants.IS_FIRST_LAUNCH, true).commit()
        addCurrencyToDatabase()

        assertEquals(
            FirstLaunchState.CONFIGURING,
            FirstLaunchStateManager.prepareForLaunch(context, isColdStart = true)
        )
        assertFalse(context.getDatabasePath(DATABASE_NAME).exists())
        assertTrue(FirstLaunchStateManager.shouldOpenSetup(context))
    }

    @Test
    fun completionUpdatesNewStateAndLegacyFlag() {
        preferences.edit()
            .putString(Constants.FIRST_LAUNCH_STATE, FirstLaunchState.CONFIGURING.name)
            .putBoolean(Constants.IS_FIRST_LAUNCH, true)
            .commit()

        FirstLaunchStateManager.completeSetup(context)

        assertEquals(FirstLaunchState.COMPLETED, FirstLaunchStateManager.getState(context))
        assertFalse(preferences.getBoolean(Constants.IS_FIRST_LAUNCH, true))
    }

    private suspend fun addCurrencyToDatabase() {
        val database = dataBase.getDataBase(context)
        try {
            database.currenciesDao().addCurrency(
                Currencies("Euro", "EUR", "EUR", null, true)
            )
        } finally {
            database.close()
        }
    }

    private companion object {
        const val DATABASE_NAME = "DataBase"
    }
}
