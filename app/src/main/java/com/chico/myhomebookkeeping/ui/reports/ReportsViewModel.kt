package com.chico.myhomebookkeeping.ui.reports

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.enums.StatesReportsRecycler
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.db.simpleQuery.ReportsCreateSimpleQuery
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCashAccountItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCategoriesItem
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCurrenciesItem
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.*

class ReportsViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsStartTimePeriodKey = Constants.FOR_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodKey = Constants.FOR_REPORTS_END_TIME_PERIOD

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var startTimePeriodLongSP = minusOneLong
    private var endTimePeriodLongSP = minusOneLong

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

//    private val _listItemsOfCategoriesForRecycler = MutableLiveData<List<ReportsCategoriesItem>>()
//    val listItemsOfCategoriesForRecycler: LiveData<List<ReportsCategoriesItem>>
//        get() = _listItemsOfCategoriesForRecycler
//
//    private val _listItemsOfCashAccountsForRecycler =
//        MutableLiveData<List<ReportsCashAccountItem>>()
//    val listItemsOfCashAccountsForRecycler: LiveData<List<ReportsCashAccountItem>>
//        get() = _listItemsOfCashAccountsForRecycler
//
//    private val _listItemsOfCurrenciesForRecycler = MutableLiveData<List<ReportsCurrenciesItem>>()
//    val listItemsOfCurrenciesForRecycler:LiveData<List<ReportsCurrenciesItem>>
//            get() = _listItemsOfCurrenciesForRecycler

    private var stateRecycler: String = StatesReportsRecycler.None.name

    private lateinit var listItemsOfCashAccounts: List<ReportsCashAccountItem>
    private lateinit var listItemsOfCategories: List<ReportsCategoriesItem>
    private lateinit var listItemsOfCurrencies: List<ReportsCurrenciesItem>

    private lateinit var selectedCategoriesSet: Set<Int>

    private lateinit var listFullMoneyMoving: Deferred<List<FullMoneyMoving>?>

    init {
        getTimePeriodsSP()
        setTextOnButtons()
        launchIo {
            getLists()
        }
    }

    private fun setTextOnButtons() {
        with(setText) {
            textOnTimePeriodButton(
                _buttonTextOfTimePeriod,
                startTimePeriodLongSP,
                endTimePeriodLongSP
            )
        }
    }

    private fun getLists(): Boolean {
        launchIo {
            listItemsOfCashAccounts = ConvToList.cashAccountsListToReportsItemsList(
                CashAccountsUseCase.getAllCashAccountsSortNameAsc(dbCashAccount)
            )
        }
        launchIo {
            getCategoriesList()
            getCategoriesSet()
        }
        launchIo {
            listItemsOfCurrencies = ConvToList.currenciesListToReportsItemsList(
                CurrenciesUseCase.getAllCurrenciesSortNameAsc(dbCurrencies)
            )
        }
        return true
    }

    private suspend fun getCategoriesSet() {
        selectedCategoriesSet = ConvToList.categoriesListToSelectedCategoriesSet(
            CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
        )
    }

    private suspend fun getCategoriesList() {
        listItemsOfCategories = ConvToList.categoriesListToReportsItemsList(
            CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
        )
    }

    private fun getTimePeriodsSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriodKey)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriodKey)
    }

    fun getMap(): MutableLiveData<Map<String, Double>?> {
        return _map
    }

    suspend fun getListOfFullMoneyMovements(): List<FullMoneyMoving>? {
        return listFullMoneyMoving.await()
    }

    private suspend fun getListOfFullMoneyMovements(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {
//        Message.log("query = ${query.sql}, args = ${query.argCount}")

        val result = MoneyMovingUseCase.getSelectedMoneyMovement(
            db, query
        )
        return result
    }

//    fun setRecyclerState(name: String) {
//        Message.log("set state RecyclerView $name")
//        stateRecycler = name
//    }
//
//    fun postCategoriesList() {
//        launchUi {
//            _listItemsOfCategoriesForRecycler.postValue(listItemsOfCategories)
//        }
//    }
//
//    fun postCashAccountsList() {
//        launchUi {
//            _listItemsOfCashAccountsForRecycler.postValue(listItemsOfCashAccounts)
//        }
//    }

//    fun postCurrenciesList() {
//        launchUi {
//            _listItemsOfCurrenciesForRecycler.postValue(listItemsOfCurrencies)
//        }
//    }
//
//    fun itemChecked(id: Int) {
//        when (stateRecycler) {
//            StatesReportsRecycler.ShowCurrencies.name -> {
//                listItemsOfCurrencies[id].isChecked = true
//            }
//            StatesReportsRecycler.ShowCategories.name -> {
//                listItemsOfCategories[id].isChecked = true
//            }
//            StatesReportsRecycler.ShowCashAccounts.name -> {
//                listItemsOfCashAccounts[id].isChecked = true
//            }
//        }
//    }

    fun itemUnchecked(id: Int) {
//        stateRecyclerMessage()
        when (stateRecycler) {
            StatesReportsRecycler.ShowCurrencies.name -> {
                listItemsOfCurrencies[id].isChecked = false
            }
            StatesReportsRecycler.ShowCategories.name -> {
                listItemsOfCurrencies[id].isChecked = false

            }
            StatesReportsRecycler.ShowCashAccounts.name -> {
                listItemsOfCashAccounts[id].isChecked = false
            }
        }
    }

//    private fun stateRecyclerMessage() {
//        Message.log("state recycler ${StatesReportsRecycler.ShowCurrencies.name}")
//    }

    suspend fun updateReports(await: Boolean) {
        runBlocking {
            val query = createQuery()

            listFullMoneyMoving = async(Dispatchers.IO) { getListOfFullMoneyMovements(query) }

            Message.log("result of get List fulMoneyMoving ${listFullMoneyMoving.await()?.joinToString()}")
//            val listMoneyMovingForReports: Deferred<List<FullMoneyMoving>?> =
//                async(Dispatchers.IO) { getListOfFullMoneyMovements(query) }
            if (!listFullMoneyMoving.await().isNullOrEmpty()) {
                _map.postValue(
                    listFullMoneyMoving.await()
                        ?.let { ConvToList.moneyMovementListToMap(it) })
            }
        }
    }

    //    private fun createQuery(): SimpleSQLiteQuery {
//        return ReportsCreateSimpleQuery.createSampleQueryForReports(
//            startTimePeriodLongSP,
//            endTimePeriodLongSP,
////            listItemsOfCashAccounts,
////            listItemsOfCurrencies,
//            listItemsOfCategories
//        )
//    }
    private fun createQuery(): SimpleSQLiteQuery {
        return ReportsCreateSimpleQuery.createSampleQueryForReports(
            startTimePeriodLong = startTimePeriodLongSP,
            endTimePeriodLong = endTimePeriodLongSP,
            setItemsOfCategories = selectedCategoriesSet
        )
    }

    suspend fun getNumbersOfCategories(): Int {
        return CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory).size
    }

    fun getListOfCategoriesItems(): List<ReportsCategoriesItem> {
        return listItemsOfCategories
    }

    suspend fun getListOfCategories(): List<Categories> {
        return CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
    }

    fun updateSelectedCategories(categoriesSet: Set<Int>): Boolean {
        selectedCategoriesSet = categoriesSet
        return true
    }
}