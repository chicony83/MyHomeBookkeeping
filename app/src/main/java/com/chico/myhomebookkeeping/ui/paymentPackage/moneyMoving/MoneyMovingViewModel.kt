package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

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
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.simpleQuery.MoneyMovingCreateSimpleQuery
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.*

class MoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val space = " "
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.ARGS_QUERY_PAYMENT_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.ARGS_QUERY_PAYMENT_CURRENCY_KEY
    private val argsCategoryKey = Constants.ARGS_QUERY_PAYMENT_CATEGORY_KEY
    private val argsIncomeSpendingKey = Constants.ARGS_QUERY_PAYMENT_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsIdMoneyMovingForChange = Constants.ARGS_CHANGE_PAYMENT_ID
    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH
    private val argsStartTimePeriod = Constants.ARGS_QUERY_PAYMENT_START_TIME_PERIOD
    private val argsEndTimePeriod = Constants.ARGS_QUERY_PAYMENT_END_TIME_PERIOD

    private val argsNewEntryOfMoneyMovingInDbIsAdded = Constants.ARGS_NEW_ENTRY_OF_MONEY_MOVING_IN_DB_IS_ADDED

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val textEmpty = Constants.TEXT_EMPTY

    private var startTimePeriodLongSP = minusOneLong
    private var endTimePeriodLongSP = minusOneLong
    private var cashAccountIntSP = -1
    private var currencyIntSP: Int = -1
    private var categoryIntSP = -1
    private var incomeSpendingStringSP: String = argsNone

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

    private val _buttonTextOfTimePeriod = MutableLiveData<String>()
    val buttonTextOfTimePeriod: LiveData<String>
        get() = _buttonTextOfTimePeriod

    private val _incomeBalance = MutableLiveData<String>()
    val incomeBalance: LiveData<String>
        get() = _incomeBalance

    private val _spendingBalance = MutableLiveData<String>()
    val spendingBalance: LiveData<String>
        get() = _spendingBalance

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String>
        get() = _totalBalance

//    private val _selectedMoneyMoving = MutableLiveData<FullMoneyMoving?>()
//    val selectedMoneyMoving: MutableLiveData<FullMoneyMoving?>
//        get() = _selectedMoneyMoving

    private val setText = SetTextOnButtons(app.resources)

    fun getListFullMoneyMoving() {
        runBlocking {
            getValuesSP()
            setTextOnButtons()
            val listFullMoneyMoving: Deferred<List<FullMoneyMoving>?> =
                async(Dispatchers.IO) { loadListOfMoneyMoving() }
            Log.i("TAG", "found lines money moving ${listFullMoneyMoving.await()?.size}")
            postListFullMoneyMoving(listFullMoneyMoving.await())
            postBalanceValues(listFullMoneyMoving.await())
        }
    }

    private fun postListFullMoneyMoving(list: List<FullMoneyMoving>?) {
        _moneyMovementList.postValue(list)
    }

    private fun setTextOnButtons() {
        with(setText) {
            textOnCategoryButton(
                _buttonTextOfQueryCategory,
                dbCategory,
                categoryIntSP,
                getSP,
                argsIncomeSpendingKey
            )
            textOnCurrencyButton(
                _buttonTextOfQueryCurrency,
                dbCurrencies,
                currencyIntSP
            )
            textOnCashAccountButton(
                _buttonTextOfQueryCashAccount,
                dbCashAccount,
                cashAccountIntSP
            )
            textOnTimePeriodButton(
                _buttonTextOfTimePeriod,
                startTimePeriodLongSP,
                endTimePeriodLongSP
            )
        }
    }

    private fun getResourceText(string: Int): String {
        return app.getString(string)
    }

    private fun getValuesSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriod)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriod)
        incomeSpendingStringSP = getSP.getString(argsIncomeSpendingKey) ?: argsNone
        cashAccountIntSP = getSP.getInt(argsCashAccountKey)
        currencyIntSP = getSP.getInt(argsCurrencyKey)
        categoryIntSP = getSP.getInt(argsCategoryKey)
    }

    private suspend fun loadListOfMoneyMoving() = launchForResult {
        val query = MoneyMovingCreateSimpleQuery.createQueryList(
            currencyIntSP,
            categoryIntSP,
            cashAccountIntSP,
            incomeSpendingStringSP,
            startTimePeriodLongSP,
            endTimePeriodLongSP
        )
        return@launchForResult getListMoneyMovement(query)
    }

    private suspend fun getListMoneyMovement(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {
        return MoneyMovingUseCase.getSelectedFullMoneyMovement(
            db,
            query
        )
    }

    private fun postBalanceValues(list: List<FullMoneyMoving>?) = runBlocking {
        if (!list.isNullOrEmpty()) {
            val moneyMovingCountMoney = MoneyMovingCountMoney(list)
            _incomeBalance.postValue(
                getResourceText(R.string.description_income) + space + moneyMovingCountMoney.getIncome()
            )
            _spendingBalance.postValue(
                getResourceText(R.string.description_spending) + space + moneyMovingCountMoney.getSpending()
            )
            _totalBalance.postValue(
                getResourceText(R.string.description_balance) + space + moneyMovingCountMoney.getBalance()
            )
        }

    }


//    fun loadSelectedMoneyMoving(selectedId: Long) {
//        launchUi {
//            _selectedMoneyMoving.postValue(
//                MoneyMovingUseCase.getOneFullMoneyMoving(
//                    db,
//                    MoneyMovingCreateSimpleQuery.createQueryOneLine(selectedId)
//                )
//            )
//        }
//    }

//    fun saveMoneyMovingToChange() {
//        setSP.setLong(argsIdMoneyMovingForChange, _selectedMoneyMoving.value?.id)
//    }

    fun cleaningSP() {
        with(setSP) {
            val argsDateTimeChangeKey = Constants.ARGS_CHANGE_PAYMENT_DATE_TIME_KEY
            val argsCashAccountChangeKey = Constants.ARGS_CHANGE_PAYMENT_CASH_ACCOUNT_KEY
            val argsCurrencyChangeKey = Constants.ARGS_CHANGE_PAYMENT_CURRENCY_KEY
            val argsCategoryChangeKey = Constants.ARGS_CHANGE_PAYMENT_CATEGORY_KEY
            val argsDescriptionChangeKey = Constants.ARGS_CHANGE_PAYMENT_DESCRIPTION_KEY
            val argsAmountChangeKey = Constants.ARGS_CHANGE_PAYMENT_AMOUNT_KEY

            saveToSP(argsDateTimeChangeKey, minusOneLong)
            saveToSP(argsCashAccountChangeKey, minusOneInt)
            saveToSP(argsCurrencyChangeKey, minusOneInt)
            saveToSP(argsCategoryChangeKey, minusOneInt)
            saveToSP(argsDescriptionChangeKey, textEmpty)
            saveToSP(argsAmountChangeKey, textEmpty)
        }
    }

    fun isFirstLaunch(): Boolean {
        return getSP.getBoolean(argsIsFirstLaunch)
//        return true
    }

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }

    private fun messageLog(text: String) {
        Log.i("TAG", text)
    }

    suspend fun loadSelectedMoneyMoving(selectedId: Long): FullMoneyMoving? {
        return MoneyMovingUseCase.getOneFullMoneyMoving(
            db,
            MoneyMovingCreateSimpleQuery.createQueryOneLine(selectedId)
        )

    }

    fun saveIdMoneyMovingForChange(selectedId: Long) {
        setSP.saveToSP(argsIdMoneyMovingForChange, selectedId)
    }

    fun isTheEntryOfMoneyMovingAdded() :Boolean {
        return getSP.getBooleanElseReturnFalse(argsNewEntryOfMoneyMovingInDbIsAdded)
    }

    fun dialogOfNewEntryAddedIsShowed() {
        setSP.saveToSP(argsNewEntryOfMoneyMovingInDbIsAdded,false)
    }

}