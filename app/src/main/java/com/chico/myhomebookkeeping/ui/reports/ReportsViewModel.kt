package com.chico.myhomebookkeeping.ui.reports

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.enums.ReportsType
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.ui.moneyMoving.MoneyMovingCreteQuery
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.*

class ReportsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsStartTimePeriodKey = Constants.FOR_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodKey = Constants.FOR_REPORTS_END_TIME_PERIOD
    private val argsCashAccountKey = Constants.FOR_REPORTS_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_REPORTS_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_REPORTS_CATEGORY_KEY
    private val argsIncomeSpendingKey = Constants.FOR_REPORTS_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsReportType = Constants.REPORT_TYPE

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var startTimePeriodLongSP = minusOneLong
    private var endTimePeriodLongSP = minusOneLong
    private var cashAccountIntSP = -1
    private var currencyIntSP: Int = -1
    private var categoryIntSP = -1
    private var incomeSpendingStringSP: String = argsNone
    private var reportsTypeStringSP = argsNone

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
    private val setTextOnButtons = SetTextOnButtons(app.resources)

    fun getListFullMoneyMoving() {
        runBlocking {
            getValuesSP()
            Message.log("argsReportType = $reportsTypeStringSP")
            if (reportsTypeStringSP == ReportsType.PieIncome.toString()) {
                incomeSpendingStringSP = Constants.FOR_QUERY_INCOME
                Message.log("income")
            }
            if (reportsTypeStringSP == ReportsType.PieSpending.toString()) {
                incomeSpendingStringSP = Constants.FOR_QUERY_SPENDING
                Message.log("spending")
            }

            val listMoneyMoving: Deferred<List<FullMoneyMoving>?> =
                async(Dispatchers.IO) { loadListOfFullMoneyMoving() }

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

    private suspend fun loadListOfFullMoneyMoving() = launchForResult {
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
        setTextOnButtons.textOnCategoryButton(
            _buttonTextOfQueryCategory,
            dbCategory,
            categoryIntSP,
            getSP,
            argsIncomeSpendingKey
        )

        setTextOnButtons.textOnCurrencyButton(
            _buttonTextOfQueryCurrency,
            dbCurrencies,
            currencyIntSP
        )

        setTextOnButtons.textOnCashAccountButton(
            _buttonTextOfQueryCashAccount,
            dbCashAccount,
            cashAccountIntSP
        )

        setTextOnButtons.textOnTimePeriodButton(
            _buttonTextOfTimePeriod,
            startTimePeriodLongSP,
            endTimePeriodLongSP
        )

    }

    private fun getValuesSP() {
        reportsTypeStringSP = getSP.getString(argsReportType) ?: argsNone
        Message.log("reportsType = $reportsTypeStringSP")
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriodKey)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriodKey)
//        incomeSpendingStringSP = getSP.getString(argsIncomeSpendingKey) ?: argsNone
        cashAccountIntSP = getSP.getInt(argsCashAccountKey)
        currencyIntSP = getSP.getInt(argsCurrencyKey)
        categoryIntSP = getSP.getInt(argsCategoryKey)
    }

}