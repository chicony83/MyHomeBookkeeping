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
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.enums.StatesReportsRecycler
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.db.simpleQuery.ReportsCreateSimpleQuery
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

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

    private val setText = SetTextOnButtons(app.resources)

    private val _itemsForReportsList = MutableLiveData<List<ReportsItem>>()
    val itemsListForRecycler: LiveData<List<ReportsItem>> get() = _itemsForReportsList

    private var stateRecycler: String = StatesReportsRecycler.None.name

    private lateinit var listItemsOfCashAccounts: List<ReportsItem>
    private lateinit var listItemsOfCategories: List<ReportsItem>
    private lateinit var listItemsOfCurrencies: List<ReportsItem>

    init {
        getTimePeriodsSP()
        getLists()
        setTextOnButtons()
    }

    private fun setTextOnButtons() {
        with(setText){
            textOnTimePeriodButton(
                _buttonTextOfTimePeriod,
                startTimePeriodLongSP,
                endTimePeriodLongSP
            )
        }
    }

    private fun getLists() {
        launchIo {
            listItemsOfCashAccounts = ConvToList.cashAccountsListToReportsItemsList(
                CashAccountsUseCase.getAllCashAccountsSortNameAsc(dbCashAccount)
            )
        }
        launchIo {
            listItemsOfCategories = ConvToList.categoriesListToReportsItemsList(
                CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
            )
        }
        launchIo {
            listItemsOfCurrencies = ConvToList.currenciesListToReportsItemsList(
                CurrenciesUseCase.getAllCurrenciesSortNameAsc(dbCurrencies)
            )
        }
    }

    private fun getTimePeriodsSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriodKey)
        Message.log("start time period = ${startTimePeriodLongSP.parseTimeFromMillisShortDate()}")
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriodKey)
        Message.log("end time period = ${endTimePeriodLongSP.parseTimeFromMillisShortDate()}")
    }

    fun getMap(): MutableLiveData<Map<String, Double>?> {
        return _map
    }

    private suspend fun getListOfFullMoneyMovements(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {
        Message.log("query = ${query.sql}, args = ${query.argCount}")

        val result = MoneyMovingUseCase.getSelectedMoneyMovement(
            db, query
        )
        return result
//        return MoneyMovingUseCase.getSelectedMoneyMovement(
//            db,
//            query
//        )
    }

    fun setRecyclerState(name: String) {
        Message.log("set state RecyclerView $name")
        stateRecycler = name
    }

    fun postCategoriesList() {
        launchUi {
            _itemsForReportsList.postValue(listItemsOfCategories)
        }
    }

    fun postCashAccountsList() {
        launchUi {
            _itemsForReportsList.postValue(listItemsOfCashAccounts)
        }
    }


    fun postCurrenciesList() {
        launchUi {
            _itemsForReportsList.postValue(listItemsOfCurrencies)
        }
    }

    fun itemChecked(id: Int) {
        when (stateRecycler) {
            StatesReportsRecycler.ShowCurrencies.name -> {
                setCheckedTrue(listItemsOfCurrencies, id)
            }
            StatesReportsRecycler.ShowCategories.name -> {
                setCheckedTrue(listItemsOfCategories, id)
            }
            StatesReportsRecycler.ShowCashAccounts.name -> {
                setCheckedTrue(listItemsOfCashAccounts, id)
            }
        }
    }

    private fun setCheckedTrue(list: List<ReportsItem>, id: Int) {
        list[id].isChecked = true
    }

    fun itemUnchecked(id: Int) {
        stateRecyclerMessage()
        when (stateRecycler) {
            StatesReportsRecycler.ShowCurrencies.name -> {
                setCheckedFalse(listItemsOfCurrencies, id)
            }
            StatesReportsRecycler.ShowCategories.name -> {
                setCheckedFalse(listItemsOfCategories, id)
            }
            StatesReportsRecycler.ShowCashAccounts.name -> {
                setCheckedFalse(listItemsOfCashAccounts, id)
            }
        }
    }

    private fun setCheckedFalse(list: List<ReportsItem>, id: Int) {
        list[id].isChecked = false
    }

    private fun stateRecyclerMessage() {
        Message.log("state recycler ${StatesReportsRecycler.ShowCurrencies.name}")
    }

    suspend fun updateReports() {
        runBlocking {
            val query = createQuery()
            val listMoneyMovingForReports: Deferred<List<FullMoneyMoving>?> =
                async(Dispatchers.IO) { getListOfFullMoneyMovements(query) }

            Message.log("size Full Money Moving list = ${listMoneyMovingForReports.await()?.size}")
            if (!listMoneyMovingForReports.await().isNullOrEmpty()) {
                _map.postValue(
                    listMoneyMovingForReports.await()
                        ?.let { ConvToList.moneyMovementListToMap(it) })
            }
        }
    }

    private fun createQuery(): SimpleSQLiteQuery {
        return ReportsCreateSimpleQuery.createSampleQueryForReports(
            startTimePeriodLongSP,
            endTimePeriodLongSP,
            listItemsOfCashAccounts,
            listItemsOfCurrencies,
            listItemsOfCategories
        )
    }
}