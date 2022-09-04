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
        endTimePeriodLongSP: Long
    ) {

        val text: String = getResourceText(R.string.text_on_button_time_period)
        var timePeriod = ""
        val textFrom = getResourceText(R.string.text_on_button_time_period_from)
        val textTo = getResourceText(R.string.text_on_button_time_period_to)
        val textAllTime = getResourceText(R.string.text_on_button_time_period_all_time)
        if (modelCheck.isPositiveValue(startTimePeriodLongSP)) {
            timePeriod =
                textFrom +
                        space +
                        startTimePeriodLongSP.parseTimeFromMillisShortDate() +
                        space
        }
        if (modelCheck.isPositiveValue(endTimePeriodLongSP)) {
            timePeriod =
                timePeriod +
                        space +
                        textTo +
                        space +
                        endTimePeriodLongSP.parseTimeFromMillisShortDate()
        }
        if ((!modelCheck.isPositiveValue(startTimePeriodLongSP))
            and (!modelCheck.isPositiveValue(endTimePeriodLongSP))
        ) {
            timePeriod = textAllTime
        }
        Message.log(timePeriod)
        launchUi {
            _buttonTextOfTimePeriod.postValue(createButtonText(text, timePeriod))
        }
    }

    fun textOnCashAccountButton(
        _buttonTextOfQueryCashAccount: MutableLiveData<String>,
        dbCashAccount: CashAccountDao,
        cashAccountIntSP: Int
    ) {

        val nameButton: String = getResourceText(R.string.text_on_button_cash_account)
        var nameCashAccount = ""
        if (modelCheck.isPositiveValue(cashAccountIntSP)) {
            launchIo {
                nameCashAccount = CashAccountsUseCase.getOneCashAccountById(
                    dbCashAccount,
                    cashAccountIntSP
                )?.accountName.toString()
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
        argsIncomeSpendingKey: String
    ) {
        val nameButton: String = getResourceText(R.string.text_on_button_category)
        var nameCategory = ""
        if (modelCheck.isPositiveValue(categoryIntSP)) {
            launchIo {
                nameCategory = CategoriesUseCase.getOneCategory(
                    dbCategory,
                    categoryIntSP
                )?.categoryName.toString()
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

    fun setTextOnSortingCategoriesButton(
        textOnButton: MutableLiveData<String>,
        additionalTextForTheButton: String,
        ) {
//        val nameButton = getResourceText(R.string.text_on_button_sorting_as)
        textOnButton.postValue(createButtonText(additionalTextForTheButton))
    }

}