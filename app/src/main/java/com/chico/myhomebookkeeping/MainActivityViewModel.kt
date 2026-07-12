package com.chico.myhomebookkeeping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.checks.AppVersion
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.sp.GetSP
import kotlinx.coroutines.runBlocking

class MainActivityViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private var sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private var getSP = GetSP(sharedPreferences)
    private val cashAccountDao = dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val categoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()
    private val currenciesDao = dataBase.getDataBase(app.applicationContext).currenciesDao()

    init {
        initializeVersionTrackingForNewInstallation()
        fixFirstLaunchFlagForExistingDatabase()
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
        val isFirstLaunch = getSP.getBooleanElseReturnTrue(Constants.IS_FIRST_LAUNCH)
        if (isFirstLaunch && hasUserCreatedData()) {
            setFirstLaunchFlag(false)
            Message.log("---is first launch = false, existing database data found---")
            return false
        }
        Message.log("---is first launch = $isFirstLaunch")
        return isFirstLaunch
    }

    fun setFirstLaunchFlag(flag: Boolean) {
        getSP.setBoolean(Constants.IS_FIRST_LAUNCH, flag)
    }

    private fun fixFirstLaunchFlagForExistingDatabase() {
        if (!sharedPreferences.contains(Constants.IS_FIRST_LAUNCH) && hasUserCreatedData()) {
            setFirstLaunchFlag(false)
        }
    }

    private fun hasUserCreatedData(): Boolean = runBlocking {
        cashAccountDao.getCashAccountsCount() > 0 ||
                categoryDao.getCategoriesCount() > 0 ||
                currenciesDao.getCurrenciesCount() > 0
    }

}
