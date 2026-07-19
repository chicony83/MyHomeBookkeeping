package com.chico.myhomebookkeeping.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving.QuickPaymentSettings

class SettingsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val sharedPreferences =
        app.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()

    private val _appVersion = MutableLiveData<String>()
    val appVersion: LiveData<String>
        get() = _appVersion

    private val _quickPaymentSettings = MutableLiveData<QuickPaymentSettings>()
    val quickPaymentSettings: LiveData<QuickPaymentSettings>
        get() = _quickPaymentSettings

    private val _startFragment = MutableLiveData<String>()
    val startFragment: LiveData<String>
        get() = _startFragment

    init {
        val currentVersion = app.getString(R.string.current_version)
        val packageInfo = app.packageManager.getPackageInfo(app.packageName, 0)
        @Suppress("DEPRECATION")
        val versionCode = packageInfo.versionCode
        _appVersion.value = "$currentVersion ${packageInfo.versionName} ($versionCode)"
        _quickPaymentSettings.value = getQuickPaymentSettings()
        _startFragment.value = getStartFragment()
    }

    fun saveQuickPaymentSettings(settings: QuickPaymentSettings) {
        sharedPreferences.edit()
            .putBoolean(
                Constants.QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL,
                settings.isCurrencyScrollEnabled
            )
            .putBoolean(
                Constants.QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL,
                settings.isCashAccountScrollEnabled
            )
            .putBoolean(
                Constants.QUICK_PAYMENT_SHOW_CALCULATOR,
                settings.isCalculatorButtonVisible
            )
            .putString(Constants.QUICK_PAYMENT_AMOUNT_INPUT_MODE, settings.amountInputMode)
            .putInt(Constants.QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS, settings.amountWholeDigits)
            .putInt(Constants.QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS, settings.amountFractionDigits)
            .apply()
        _quickPaymentSettings.value = settings
    }

    fun saveStartFragment(startFragment: String) {
        sharedPreferences.edit()
            .putString(Constants.START_FRAGMENT, startFragment)
            .apply()
        _startFragment.value = startFragment
    }

    suspend fun getAllCurrencies(): List<Currencies> {
        return CurrenciesUseCase.getAllCurrenciesSortNameAsc(dbCurrencies)
    }

    suspend fun getAllCashAccounts(): List<CashAccount> {
        return CashAccountsUseCase.getAllCashAccountsSortNameAsc(dbCashAccount)
    }

    suspend fun getDefaultCurrency(): Currencies? {
        return CurrenciesUseCase.getDefaultCurrency(dbCurrencies)
    }

    suspend fun getDefaultCashAccount(): CashAccount? {
        return CashAccountsUseCase.getDefaultCashAccount(dbCashAccount)
    }

    suspend fun setDefaultCurrency(currency: Currencies) {
        currency.currencyId?.let { CurrenciesUseCase.setDefaultCurrency(dbCurrencies, it) }
    }

    suspend fun setDefaultCashAccount(cashAccount: CashAccount) {
        cashAccount.cashAccountId?.let { CashAccountsUseCase.setDefaultCashAccount(dbCashAccount, it) }
    }

    private fun getQuickPaymentSettings(): QuickPaymentSettings {
        return QuickPaymentSettings(
            isCurrencyScrollEnabled = sharedPreferences.getBoolean(
                Constants.QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL,
                false
            ),
            isCashAccountScrollEnabled = sharedPreferences.getBoolean(
                Constants.QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL,
                false
            ),
            isCalculatorButtonVisible = sharedPreferences.getBoolean(
                Constants.QUICK_PAYMENT_SHOW_CALCULATOR,
                true
            ),
            amountInputMode = sharedPreferences.getString(
                Constants.QUICK_PAYMENT_AMOUNT_INPUT_MODE,
                Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS
            )?.takeIf { it.isNotBlank() } ?: Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS,
            amountWholeDigits = sharedPreferences.getInt(
                Constants.QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS,
                Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS
            ).coerceIn(1, 9),
            amountFractionDigits = sharedPreferences.getInt(
                Constants.QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS,
                Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
            ).coerceIn(0, 4)
        )
    }

    private fun getStartFragment(): String {
        return sharedPreferences.getString(
            Constants.START_FRAGMENT,
            Constants.START_FRAGMENT_FAST_PAYMENTS
        ) ?: Constants.START_FRAGMENT_FAST_PAYMENTS
    }
}
