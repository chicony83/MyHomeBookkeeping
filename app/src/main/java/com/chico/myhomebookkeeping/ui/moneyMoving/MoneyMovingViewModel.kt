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
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.checks.GetSP
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
import com.chico.myhomebookkeeping.helpers.SetSP
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.*

class MoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_QUERY_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsIncomeSpending = Constants.FOR_QUERY_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsIdMoneyMovingForChange = Constants.FOR_CHANGE_ID_MONEY_MOVING
    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH

    private val modelCheck = ModelCheck()
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
    private val setSP = SetSP(spEditor)

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var getSP = GetSP(sharedPreferences)

    private val _moneyMovementList = MutableLiveData<List<FullMoneyMoving>?>()
    val moneyMovementList: MutableLiveData<List<FullMoneyMoving>?>
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

    private val _incomeBalance = MutableLiveData<String>()
    val incomeBalance: LiveData<String>
        get() = _incomeBalance

    private val _spendingBalance = MutableLiveData<String>()
    val spendingBalance: LiveData<String>
        get() = _spendingBalance

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String>
        get() = _totalBalance

    private val _selectedMoneyMoving = MutableLiveData<FullMoneyMoving?>()
    val selectedMoneyMoving: MutableLiveData<FullMoneyMoving?>
        get() = _selectedMoneyMoving

    private var cashAccountSP = -1
    private var currencySP: Int = -1
    private var categorySP = -1
    private var incomeSpendingSP: String = argsNone

//    private lateinit var moneyMovingCountMoney:MoneyMovingCountMoney

    //    private lateinit var resultQuery: List<FullMoneyMoving>?
    init {
        launchUi {
            setTextOnButtons()
        }

        runBlocking {
            getValuesSP()
            val listFullMoneyMoving: Deferred<List<FullMoneyMoving>?> =
                async(Dispatchers.IO) { loadListMoneyMovement() }
            Log.i("TAG", "found lines money moving ${listFullMoneyMoving.await()?.size}")
            postListFullMoneyMoving(listFullMoneyMoving.await())
            postBalanceValues(listFullMoneyMoving.await())
        }
    }

    private fun postListFullMoneyMoving(list: List<FullMoneyMoving>?) {
        _moneyMovementList.postValue(list)
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
            if (modelCheck.isPositiveValue(cashAccountSP)) {
                name = CashAccountsUseCase.getOneCashAccount(
                    dbCashAccount,
                    cashAccountSP
                )?.accountName.toString()
            }
            if (!modelCheck.isPositiveValue(cashAccountSP)) {
                name = getResourceText(R.string.all_text)
            }
            _buttonTextOfQueryCashAccount.postValue(createButtonText(text, name))
        }
    }

    private fun setTextOnCurrencyButton() {
        launchUi {
            val text: String = getResourceText(R.string.currency_text)
            var name: String = ""

            if (modelCheck.isPositiveValue(currencySP)) {
                name = CurrenciesUseCase.getOneCurrency(
                    dbCurrencies,
                    currencySP
                )?.currencyName.toString()
            }
            if (!modelCheck.isPositiveValue(currencySP)) {
                name = getResourceText(R.string.all_text)
            }
            _buttonTextOfQueryCurrency.postValue(createButtonText(text, name))
        }
    }

    private fun setTextOnCategoryButton() {
        launchUi {
            val text: String = getResourceText(R.string.category_text)
            var name: String = ""
            if (modelCheck.isPositiveValue(categorySP)) {
                name = CategoriesUseCase.getOneCategory(
                    dbCategory,
                    categorySP
                )?.categoryName.toString()
            }
            if (getSP.isCategoryNone(argsIncomeSpending)) {
                if (!modelCheck.isPositiveValue(categorySP)) {
                    name = getResourceText(R.string.all_text)
                }
            }
            if (!getSP.isCategoryNone(argsIncomeSpending)) {
                if (getSP.isCategoryIncome(argsIncomeSpending)) {
                    name = getResourceText(R.string.allIncome)
                    Log.i("TAG", "income message")
                }
                if (getSP.isCategorySpending(argsIncomeSpending)) {
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
        incomeSpendingSP = getSP.getString(argsIncomeSpending) ?: argsNone
        cashAccountSP = getSP.getInt(argsCashAccountKey)
        currencySP = getSP.getInt(argsCurrencyKey)
        categorySP = getSP.getInt(argsCategoryKey)
    }

    private suspend fun loadListMoneyMovement() = runBlocking {

        val query = MoneyMovingCreteQuery.createQueryList(
            currencySP,
            categorySP,
            cashAccountSP,
            incomeSpendingSP
        )
        return@runBlocking getListMoneyMovement(query)
    }

    private suspend fun getListMoneyMovement(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {

        return MoneyMovingUseCase.getSelectedMoneyMovement(
            db,
            query
        )
    }

    private fun postBalanceValues(list: List<FullMoneyMoving>?) = runBlocking {
        if (!list.isNullOrEmpty()){
            val moneyMovingCountMoney = MoneyMovingCountMoney(list)
            _incomeBalance.postValue(
                getResourceText(R.string.income) + " " + moneyMovingCountMoney.getIncome()
            )
            _spendingBalance.postValue(
                getResourceText(R.string.spending) + " " + moneyMovingCountMoney.getSpending()
            )
            _totalBalance.postValue(
                getResourceText(R.string.balance) + " " + moneyMovingCountMoney.getBalance()
            )
        }

    }

    private fun postIncomeBalance(await: MoneyMovingCountMoney?) {
        _incomeBalance.postValue(await?.getIncome())
    }

    private fun isOneCurrency(): Boolean {
        return currencySP > 0
    }

    fun getNumFoundLines(): Int {
        return 0
//        return foundLines
    }

//    fun isMoneyMovementFound(): Boolean {
//        return foundLines > 0
//    }

    fun loadSelectedMoneyMoving(selectedId: Long) {
        launchUi {
            _selectedMoneyMoving.postValue(
                MoneyMovingUseCase.getOneFullMoneyMoving(
                    db,
                    MoneyMovingCreteQuery.createQueryOneLine(selectedId)
                )
            )
        }
    }

    fun saveMoneyMovingToChange() {
        setSP.setLong(argsIdMoneyMovingForChange, _selectedMoneyMoving.value?.id)
    }

    fun cleaningSP() {
        with(setSP) {
            val argsDateTimeChangeKey = Constants.FOR_CHANGE_DATE_TIME_KEY
            val argsCashAccountChangeKey = Constants.FOR_CHANGE_CASH_ACCOUNT_KEY
            val argsCurrencyChangeKey = Constants.FOR_CHANGE_CURRENCY_KEY
            val argsCategoryChangeKey = Constants.FOR_CHANGE_CATEGORY_KEY
            val argsDescriptionChangeKey = Constants.FOR_CHANGE_DESCRIPTION_KEY
            val argsAmountChangeKey = Constants.FOR_CHANGE_AMOUNT_KEY

            saveToSP(argsDateTimeChangeKey, minusOneLong)
            saveToSP(argsCashAccountChangeKey, minusOneInt)
            saveToSP(argsCurrencyChangeKey, minusOneInt)
            saveToSP(argsCategoryChangeKey, minusOneInt)
            saveToSP(argsDescriptionChangeKey, "")
            saveToSP(argsAmountChangeKey, "")
        }
    }

    fun isFirstLaunch(): Boolean {
        return getSP.getBoolean(argsIsFirstLaunch)
    }

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }
}