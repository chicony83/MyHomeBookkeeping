package com.chico.myhomebookkeeping

import android.app.Application
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chico.myhomebookkeeping.checks.AppVersion
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.FastPaymentsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VersionHistoryInstrumentedTest {
    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }
    private val preferences by lazy {
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    }

    @Test
    fun freshInstallIsInitializedAsAlreadySeen() {
        preferences.edit().clear().commit()
        MainActivityViewModel(context.applicationContext as Application)
        assertEquals(
            AppVersion.code(context),
            preferences.getInt(ConstantsOfUpdate.LAST_CHECKED_VERSION, -1)
        )
    }

    @Test
    fun updateIsShownOnceAndThenMarkedAsSeen() {
        val currentVersion = AppVersion.code(context)
        preferences.edit()
            .clear()
            .putBoolean(Constants.IS_FIRST_LAUNCH, false)
            .putInt(ConstantsOfUpdate.LAST_CHECKED_VERSION, currentVersion - 1)
            .commit()
        val viewModel = FastPaymentsViewModel(context.applicationContext as Application)
        assertFalse(viewModel.isLastVersionOfProgramChecked())
        viewModel.setLastVersionChecked()
        assertTrue(viewModel.isLastVersionOfProgramChecked())
    }
}
