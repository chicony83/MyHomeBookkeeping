package com.chico.myhomebookkeeping.helpers

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate

class SetTextOnButtons(val resources: Resources) {

    private val modelCheck = ModelCheck()
    private val space = " "

    fun textOnTimePeriodButton(
        _buttonTextOfTimePeriod: MutableLiveData<String>,
        startTimePeriodLongSP: Long,
        endTimePeriodLongSP: Long,
        timePeriodMode: String = Constants.TIME_PERIOD_MODE_CUSTOM
    ) {

        val text: String = getResourceText(R.string.text_on_button_time_period)
        val presetText = timePeriodModeText(timePeriodMode)
        var timePeriod = ""
        val textFrom = getResourceText(R.string.text_on_button_time_period_from)
        val textTo = getResourceText(R.string.text_on_button_time_period_to)
        val textAllTime = getResourceText(R.string.text_on_button_time_period_all_time)
        if (presetText.isNotEmpty()) {
            timePeriod = presetText
        }
        if (presetText.isEmpty() && modelCheck.isPositiveValue(startTimePeriodLongSP)) {
            timePeriod =
                textFrom +
                        space +
                        startTimePeriodLongSP.parseTimeFromMillisShortDate() +
                        space
        }
        if (presetText.isEmpty() && modelCheck.isPositiveValue(endTimePeriodLongSP)) {
            timePeriod =
                timePeriod +
                        space +
                        textTo +
                        space +
                        endTimePeriodLongSP.parseTimeFromMillisShortDate()
        }
        if (presetText.isEmpty() && (!modelCheck.isPositiveValue(startTimePeriodLongSP))
            and (!modelCheck.isPositiveValue(endTimePeriodLongSP))
        ) {
            timePeriod = textAllTime
        }
        Message.log(timePeriod)
        launchUi {
            _buttonTextOfTimePeriod.postValue(createButtonText(text, timePeriod))
        }
    }

    fun timePeriodModeText(timePeriodMode: String): String {
        return when (timePeriodMode) {
            Constants.TIME_PERIOD_MODE_ALL_TIME -> getResourceText(R.string.text_on_button_time_period_all_time)
            Constants.TIME_PERIOD_MODE_THIS_WEEK -> getResourceText(R.string.text_on_button_time_period_this_week)
            Constants.TIME_PERIOD_MODE_LAST_WEEK -> getResourceText(R.string.text_on_button_time_period_last_week)
            Constants.TIME_PERIOD_MODE_THIS_MONTH -> getResourceText(R.string.text_on_button_time_period_this_month)
            Constants.TIME_PERIOD_MODE_LAST_MONTH -> getResourceText(R.string.text_on_button_time_period_last_month)
            Constants.TIME_PERIOD_MODE_LAST_28_DAYS -> getResourceText(R.string.text_on_button_time_period_last_28_days)
            Constants.TIME_PERIOD_MODE_LAST_30_DAYS -> getResourceText(R.string.text_on_button_time_period_last_30_days)
            Constants.TIME_PERIOD_MODE_LAST_90_DAYS -> getResourceText(R.string.text_on_button_time_period_last_90_days)
            Constants.TIME_PERIOD_MODE_LAST_180_DAYS -> getResourceText(R.string.text_on_button_time_period_last_180_days)
            Constants.TIME_PERIOD_MODE_LAST_365_DAYS -> getResourceText(R.string.text_on_button_time_period_last_365_days)
            else -> ""
        }
    }

    fun textOnCashAccountButton(
        _buttonTextOfQueryCashAccount: MutableLiveData<String>,
        dbCashAccount: CashAccountDao,
        cashAccountIntSP: Int,
        languageTag: String = Constants.APP_LANGUAGE_ENGLISH
    ) {

        val nameButton: String = getResourceText(R.string.text_on_button_cash_account)
        var nameCashAccount = ""
        if (modelCheck.isPositiveValue(cashAccountIntSP)) {
            launchIo {
                nameCashAccount = CashAccountsUseCase.getOneCashAccountById(
                    dbCashAccount,
                    cashAccountIntSP
                )?.displayName(languageTag).toString()
            }
        }
        if (!modelCheck.isPositiveValue(cashAccountIntSP)) {
            nameCashAccount = getResourceText(R.string.text_on_button_all_text)
        }
        launchUi {
            _buttonTextOfQueryCashAccount.postValue(createButtonText(nameButton, nameCashAccount))
        }
    }

    fun textOnCurrencyButton(
        _buttonTextOfQueryCurrency: MutableLiveData<String>,
        dbCurrencies: CurrenciesDao,
        currencyIntSP: Int
    ) {

        val nameButton: String = getResourceText(R.string.text_on_button_currency)
        var nameCurrency = ""

        if (modelCheck.isPositiveValue(currencyIntSP)) {
            launchIo {
                nameCurrency = CurrenciesUseCase.getOneCurrency(
                    dbCurrencies,
                    currencyIntSP
                )?.currencyName.toString()
            }
        }
        if (!modelCheck.isPositiveValue(currencyIntSP)) {
            nameCurrency = getResourceText(R.string.text_on_button_all_text)
        }
        launchUi {
            _buttonTextOfQueryCurrency.postValue(
                createButtonText(nameButton, nameCurrency)
            )
        }
    }

    fun textOnCategoryButton(
        _buttonTextOfQueryCategory: MutableLiveData<String>,
        dbCategory: CategoryDao,
        categoryIntSP: Int,
        getSP: GetSP,
        argsIncomeSpendingKey: String,
        languageTag: String = Constants.APP_LANGUAGE_ENGLISH
    ) {
        val nameButton: String = getResourceText(R.string.text_on_button_category)
        var nameCategory = ""
        if (modelCheck.isPositiveValue(categoryIntSP)) {
            launchIo {
                nameCategory = CategoriesUseCase.getOneCategory(
                    dbCategory,
                    categoryIntSP
                )?.displayName(languageTag).toString()
            }
        }
        if (getSP.isIncomeSpendingNone(argsIncomeSpendingKey)) {
            if (!modelCheck.isPositiveValue(categoryIntSP)) {
                nameCategory = getResourceText(R.string.text_on_button_all_text)
            }
        }
        if (!getSP.isIncomeSpendingNone(argsIncomeSpendingKey)) {
            if (getSP.isCategoryIncome(argsIncomeSpendingKey)) {
                nameCategory = getResourceText(R.string.text_on_button_all_income)
                Log.i("TAG", "income message")
            }
            if (getSP.isCategorySpending(argsIncomeSpendingKey)) {
                Log.i("TAG", "income spending")
                nameCategory = getResourceText(R.string.text_on_button_all_spending)
            }
        }
        launchUi {
            _buttonTextOfQueryCategory.postValue(createButtonText(nameButton, nameCategory))
        }
    }

    private fun createButtonText(text: String, name: String): String {
        val separator: String = getNewLineSeparator()
        return text + separator + name
    }

    private fun createButtonText(text: String): String {
//        val separator: String = getNewLineSeparator()
        return text
    }

    private fun getResourceText(string: Int): String {
        return resources.getString(string)
    }

    private fun getNewLineSeparator(): String {
        return "\n"
    }

}
