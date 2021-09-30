package com.chico.myhomebookkeeping.ui.reports

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.ui.moneyMoving.MoneyMovingCreteQuery
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate
import kotlinx.coroutines.*

class ReportsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME

    private val space = " "
    private val argsStartTimePeriodKey = Constants.FOR_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodKey = Constants.FOR_REPORTS_END_TIME_PERIOD
    private val argsCashAccountKey = Constants.FOR_REPORTS_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_REPORTS_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_REPORTS_CATEGORY_KEY
    private val argsIncomeSpendingKey = Constants.FOR_REPORTS_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var startTimePeriodLongSP = minusOneLong
    private var endTimePeriodLongSP = minusOneLong
    private var cashAccountIntSP = -1
    private var currencyIntSP: Int = -1
    private var categoryIntSP = -1
    private var incomeSpendingStringSP: String = argsNone

    private val modelCheck = ModelCheck()
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private var getSP = GetSP(sharedPreferences)

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val _buttonTextOfQueryCurrency = MutableLiveData<String>()
    val buttonTextOfQueryCurrency: LiveData<String>
        get() = _buttonTextOfQueryCurrency

    private val _buttonTextOfQueryCategory = MutableLiveData<String>()
    val buttonTextOfQueryCategory: LiveData<String>
        get() = _buttonTextOfQueryCategory

    private val _buttonTextOfQueryCashAccount = MutableLiveData<String>()
    val buttonTextOfQueryCashAccount: LiveData<String>
        get() = _buttonTextOfQueryCashAccount

    private val _buttonTextOfTimePeriod = MutableLiveData<String>()
    val buttonTextOfTimePeriod: LiveData<String>
        get() = _buttonTextOfTimePeriod

    private val _map = MutableLiveData<Map<String, Double>?>()
    val map: LiveData<Map<String, Double>?>
        get() = _map

    fun getListFullMoneyMoving() {
        runBlocking {
            getValuesSP()
//            if ((currencyIntSP > 0) and (cashAccountIntSP > 0)) {
            val listMoneyMoving: Deferred<List<FullMoneyMoving>?> =
                async(Dispatchers.IO) { loadListOfMoneyMoving() }

            if (!listMoneyMoving.await().isNullOrEmpty()) {
                Message.log("list size ${listMoneyMoving.await()?.size.toString()}")
                runBlocking {

                    val map: Map<String, Double> =
                        listMoneyMoving.await()!!.sortedBy { it.categoryNameValue }
                            .groupBy { it.categoryNameValue }
                            .mapValues {
                                it.value.sumOf { it.amount }
                            }

                    Message.log("map size ${map.size}")
                    _map.postValue(map)
                }
            }
        }
    }

    fun getMap(): MutableLiveData<Map<String, Double>?> {
        return _map
    }

    private suspend fun loadListOfMoneyMoving() = launchForResult {
        val query = MoneyMovingCreteQuery.createQueryListForReports(
            currencyIntSP,
            cashAccountIntSP,
            incomeSpendingStringSP,
            startTimePeriodLongSP,
            endTimePeriodLongSP
        )
        return@launchForResult getListMoneyMovement(query)
    }

    private suspend fun getListMoneyMovement(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {
        return MoneyMovingUseCase.getSelectedMoneyMovement(
            db,
            query
        )
    }

    fun setTextOnButtons() {
        setTextOnCategoryButton()
        setTextOnCurrencyButton()
        setTextOnCashAccountButton()
        setTextOnTimePeriodButton()
    }

    private fun setTextOnTimePeriodButton() {
        launchUi {
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
            _buttonTextOfTimePeriod.postValue(createButtonText(text, timePeriod))
        }
    }

    private fun setTextOnCashAccountButton() {
        launchUi {
            val nameButton: String = getResourceText(R.string.text_on_button_cash_account)
            var nameCashAccount = ""
            if (modelCheck.isPositiveValue(cashAccountIntSP)) {
                nameCashAccount = CashAccountsUseCase.getOneCashAccount(
                    dbCashAccount,
                    cashAccountIntSP
                )?.accountName.toString()
            }
            if (!modelCheck.isPositiveValue(cashAccountIntSP)) {
                nameCashAccount = getResourceText(R.string.text_on_button_all_text)
            }
            _buttonTextOfQueryCashAccount.postValue(createButtonText(nameButton, nameCashAccount))
        }
    }

    private fun setTextOnCurrencyButton() {
        launchUi {
            val nameButton: String = getResourceText(R.string.text_on_button_currency)
            var nameCurrency = ""

            if (modelCheck.isPositiveValue(currencyIntSP)) {
                nameCurrency = CurrenciesUseCase.getOneCurrency(
                    dbCurrencies,
                    currencyIntSP
                )?.currencyName.toString()
            }
            if (!modelCheck.isPositiveValue(currencyIntSP)) {
                nameCurrency = getResourceText(R.string.text_on_button_all_text)
            }
            _buttonTextOfQueryCurrency.postValue(createButtonText(nameButton, nameCurrency))
        }
    }

    private fun setTextOnCategoryButton() {
        launchUi {
            val nameButton: String = getResourceText(R.string.text_on_button_category)
            var nameCategory = ""
            if (modelCheck.isPositiveValue(categoryIntSP)) {
                nameCategory = CategoriesUseCase.getOneCategory(
                    dbCategory,
                    categoryIntSP
                )?.categoryName.toString()
            }
            if (getSP.isCategoryNone(argsIncomeSpendingKey)) {
                if (!modelCheck.isPositiveValue(categoryIntSP)) {
                    nameCategory = getResourceText(R.string.text_on_button_all_text)
                }
            }
            if (!getSP.isCategoryNone(argsIncomeSpendingKey)) {
                if (getSP.isCategoryIncome(argsIncomeSpendingKey)) {
                    nameCategory = getResourceText(R.string.text_on_button_all_income)
                    Log.i("TAG", "income message")
                }
                if (getSP.isCategorySpending(argsIncomeSpendingKey)) {
                    Log.i("TAG", "income spending")
                    nameCategory = getResourceText(R.string.text_on_button_all_spending)
                }
            }
            _buttonTextOfQueryCategory.postValue(createButtonText(nameButton, nameCategory))
        }
    }

    private fun createButtonText(text: String, name: String): String {
        val separator: String = getNewLineSeparator()
        return text + separator + name
    }

    private fun getResourceText(string: Int): String {
        return app.getString(string)
    }

    private fun getNewLineSeparator(): String {
        return "\n"
    }

    private fun getValuesSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriodKey)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriodKey)
        incomeSpendingStringSP = getSP.getString(argsIncomeSpendingKey) ?: argsNone
        cashAccountIntSP = getSP.getInt(argsCashAccountKey)
        currencyIntSP = getSP.getInt(argsCurrencyKey)
        categoryIntSP = getSP.getInt(argsCategoryKey)
    }

}