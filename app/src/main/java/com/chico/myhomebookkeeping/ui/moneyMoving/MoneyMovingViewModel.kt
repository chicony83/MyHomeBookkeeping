package com.chico.myhomebookkeeping.ui.moneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ViewModelCheck
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.runBlocking

class MoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_QUERY_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsIncomeSpending = Constants.FOR_QUERY_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()

    private var viewModelCheck = ViewModelCheck(sharedPreferences)

    private val _moneyMovementList = MutableLiveData<List<FullMoneyMoving>>()
    val moneyMovementList: MutableLiveData<List<FullMoneyMoving>>
        get() = _moneyMovementList

    private val _buttonTextOfQueryCurrency = MutableLiveData<String>()
    val buttonTextOfQueryCurrency: LiveData<String>
        get() = _buttonTextOfQueryCurrency

    private val _buttonTextOfQueryCategory = MutableLiveData<String>()
    val buttonTextOfQueryCategory: LiveData<String>
        get() = _buttonTextOfQueryCategory

    private val _buttonTextOfQueryCashAccount = MutableLiveData<String>()
    val buttonTextOfQueryCashAccount: LiveData<String>
        get() = _buttonTextOfQueryCashAccount

    private val _amountMoneyOfQuery = MutableLiveData<String>()
    val amountMoneyOfQuery: LiveData<String>
        get() = _amountMoneyOfQuery

    private val _selectedMoneyMoving = MutableLiveData<FullMoneyMoving?>()
    val selectedMoneyMoving: MutableLiveData<FullMoneyMoving?>
        get() = _selectedMoneyMoving

    private var cashAccountSP = -1
    private var currencySP: Int = -1
    private var categorySP = -1
    private var incomeSpendingSP: String = argsNone

    private var foundLines = 0

    private lateinit var resultQuery: List<FullMoneyMoving>

    init {
        runBlocking {
            getValuesSP()
            foundLines = loadMoneyMovement()
            setTextOnButtons()
        }
    }

    private fun setTextOnButtons() {
        setTextOnCategoryButton()
        setTextOnCurrencyButton()
        setTextOnCashAccountButton()
    }

    private fun setTextOnCashAccountButton() {
        launchUi {
            val text: String = getResourceText(R.string.cash_account_text)
            var name: String = ""
            if (viewModelCheck.isPositiveValue(cashAccountSP)) {
                name = CashAccountsUseCase.getOneCashAccount(
                    dbCashAccount,
                    cashAccountSP
                )?.accountName.toString()
            }
            if (!viewModelCheck.isPositiveValue(cashAccountSP)) {
                name = getResourceText(R.string.all_text)
            }
            _buttonTextOfQueryCashAccount.postValue(createButtonText(text, name))
        }
    }

    private fun setTextOnCurrencyButton() {
        launchUi {
            val text: String = getResourceText(R.string.currency_text)
            var name: String = ""

            if (viewModelCheck.isPositiveValue(currencySP)) {
                name = CurrenciesUseCase.getOneCurrency(
                    dbCurrencies,
                    currencySP
                )?.currencyName.toString()
            }
            if (!viewModelCheck.isPositiveValue(currencySP)) {
                name = getResourceText(R.string.all_text)
            }
            _buttonTextOfQueryCurrency.postValue(createButtonText(text, name))
        }
    }

    private fun setTextOnCategoryButton() {
        launchUi {
            val text: String = getResourceText(R.string.category_text)
            var name: String = ""
            if (viewModelCheck.isPositiveValue(categorySP)) {
                name = CategoriesUseCase.getOneCategory(
                    dbCategory,
                    categorySP
                )?.categoryName.toString()
            }
            if (viewModelCheck.isCategoryNone(argsIncomeSpending)) {
                if (!viewModelCheck.isPositiveValue(categorySP)) {
                    name = getResourceText(R.string.all_text)
                }
            }
            if (!viewModelCheck.isCategoryNone(argsIncomeSpending)) {
                if (viewModelCheck.isCategoryIncome(argsIncomeSpending)) {
                    name = getResourceText(R.string.allIncome)
                    Log.i("TAG", "income message")
                }
                if (viewModelCheck.isCategorySpending(argsIncomeSpending)) {
                    Log.i("TAG", "income spending")
                    name = getResourceText(R.string.allSpending)
                }
            }
            _buttonTextOfQueryCategory.postValue(createButtonText(text, name))
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
        incomeSpendingSP = viewModelCheck.getStringValueSP(argsIncomeSpending) ?: argsNone
        cashAccountSP = viewModelCheck.getValueSP(argsCashAccountKey)
        currencySP = viewModelCheck.getValueSP(argsCurrencyKey)
        categorySP = viewModelCheck.getValueSP(argsCategoryKey)

        Log.i(
            "TAG", "incomeSpending = $incomeSpendingSP, cashAccountSP = $cashAccountSP," +
                    "currencySP = $currencySP, categorySP = $categorySP"
        )
    }

    private fun loadMoneyMovement(): Int {

        val query = MoneyMovingCreteQuery.createQueryList(
            currencySP,
            categorySP,
            cashAccountSP,
            incomeSpendingSP
        )

        getResultQuery(query)
        return resultQuery.size
    }

    private fun getResultQuery(query: SimpleSQLiteQuery) = runBlocking {
        val result: List<FullMoneyMoving>? = MoneyMovingUseCase.getSelectedMoneyMovement(db, query)
        if (result != null) {
            resultQuery = result
            if (result.isNotEmpty()) {
                launchUi {
                    with(_moneyMovementList) { postValue(result) }
                }
            }
            if (isOneCurrency()) {
                launchUi {
                    _amountMoneyOfQuery.postValue(
                        MoneyMovingCountMoney.count(fullMoneyMoving = result).toString()
                    )
                }
            }
            if (!isOneCurrency()) {
                _amountMoneyOfQuery.postValue("для отображения итогов выберите валюту")
            }
        }
    }

    private fun isOneCurrency(): Boolean {
        return currencySP > 0
    }

    fun getNumFoundLines(): Int {
        return foundLines
    }

    fun isMoneyMovementFound(): Boolean {
        return foundLines > 0
    }

    fun loadSelectedMoneyMoving(selectedId: Long) {
        val query = MoneyMovingCreteQuery.createQueryOneLine(selectedId)
        launchUi {
            _selectedMoneyMoving.postValue(MoneyMovingUseCase.getOneFullMoneyMoving(db, query))
        }
    }
}