package com.chico.myhomebookkeeping.ui.reports.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
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
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.db.simpleQuery.ReportsCreateSimpleQuery
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import com.chico.myhomebookkeeping.data.reports.ReportsCashAccountItem
import com.chico.myhomebookkeeping.data.reports.ReportsCurrenciesItem
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ReportsMainViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsStartTimePeriodKey = Constants.ARGS_REPORTS_START_TIME_PERIOD
    private val argsEndTimePeriodKey = Constants.ARGS_REPORTS_END_TIME_PERIOD
    private val argsReportTypeKey = Constants.REPORT_TYPE

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

    private val _reportTitle = MutableLiveData<String>()
    val reportTitle: LiveData<String>
        get() = _reportTitle

    private val _donutCenterLabel = MutableLiveData<String>()
    val donutCenterLabel: LiveData<String>
        get() = _donutCenterLabel

    private val _periodText = MutableLiveData<String>()
    val periodText: LiveData<String>
        get() = _periodText

    private val _totalAmountText = MutableLiveData<String>()
    val totalAmountText: LiveData<String>
        get() = _totalAmountText

    private val _summaryDetailsText = MutableLiveData<String>()
    val summaryDetailsText: LiveData<String>
        get() = _summaryDetailsText

    private val _currencyShortName = MutableLiveData<String?>()
    val currencyShortName: LiveData<String?>
        get() = _currencyShortName

    private val _categoriesFilterText = MutableLiveData<String>()
    val categoriesFilterText: LiveData<String>
        get() = _categoriesFilterText

    private val _reportCategoryItems = MutableLiveData<List<ReportCategoryItem>>()
    val reportCategoryItems: LiveData<List<ReportCategoryItem>>
        get() = _reportCategoryItems

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val setText = SetTextOnButtons(app.resources)

    private lateinit var listItemsOfCashAccounts: List<ReportsCashAccountItem>
    private lateinit var listItemsOfCurrencies: List<ReportsCurrenciesItem>

    private lateinit var selectedCategoriesSet: Set<Int>

    private lateinit var listFullMoneyMoving: Deferred<List<FullMoneyMoving>?>
    private var numbersOfAllCategories = 0
    private var reportType = getSP.getString(argsReportTypeKey)
    private var paymentTypeId: Int? = ReportsCreateSimpleQuery.paymentTypeIdForReportType(reportType)

    init {
        getTimePeriodsSP()
        setTextOnButtons()
        setReportTexts()
        launchIo {
            getLists()
        }
        launchIo {
            getNumbersOfAllCategories()
        }
    }

    private suspend fun getNumbersOfAllCategories() {
        numbersOfAllCategories = CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory).size
    }

    private fun setTextOnButtons() {
        with(setText) {
            textOnTimePeriodButton(
                _buttonTextOfTimePeriod,
                startTimePeriodLongSP,
                endTimePeriodLongSP
            )
        }
        _periodText.postValue(createPeriodText())
    }

    private fun setReportTexts() {
        reportType = getSP.getString(argsReportTypeKey)
        paymentTypeId = ReportsCreateSimpleQuery.paymentTypeIdForReportType(reportType)
        _reportTitle.postValue(
            when (paymentTypeId) {
                PaymentTypeIds.INCOME -> app.getString(com.chico.myhomebookkeeping.R.string.report_title_income)
                PaymentTypeIds.SPENDING -> app.getString(com.chico.myhomebookkeeping.R.string.report_title_spending)
                else -> app.getString(com.chico.myhomebookkeeping.R.string.report_menu_title)
            }
        )
        _donutCenterLabel.postValue(
            when (paymentTypeId) {
                PaymentTypeIds.INCOME -> app.getString(com.chico.myhomebookkeeping.R.string.report_donut_center_income)
                PaymentTypeIds.SPENDING -> app.getString(com.chico.myhomebookkeeping.R.string.report_donut_center_spending)
                else -> app.getString(com.chico.myhomebookkeeping.R.string.report_total)
            }
        )
    }

    private fun getLists(): Boolean {
        launchIo {
            listItemsOfCashAccounts = ConvToList.cashAccountsListToReportsItemsList(
                CashAccountsUseCase.getAllCashAccountsSortNameAsc(dbCashAccount),
                AppLanguage.getSelectedTag(app.applicationContext)
            )
        }
        launchIo {
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
        val result: Set<String>? =
            getSP.getSelectedCategoriesSet(selectedCategoriesSetKey())?.toSet()
        val hasSavedSelection = getSP.contains(selectedCategoriesSetKey())
//        Message.log("---size of result = ${result?.size}")

        if (!result.isNullOrEmpty()) {
            val set: Set<Int> = result.map {
                it.toInt()
            }.toSet()
//            Message.log("---size of get selected categories = ${set.size}")

            selectedCategoriesSet = set
//            Message.log("selectedSet = ${selectedCategoriesSet.joinToString()}")
        }
        if (result.isNullOrEmpty() && !hasSavedSelection) {
            selectedCategoriesSet = ConvToList.categoriesListToSelectedCategoriesSet(
                CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
            )
        }
        if (result.isNullOrEmpty() && hasSavedSelection) {
            selectedCategoriesSet = emptySet()
        }
    }

    private fun getTimePeriodsSP() {
        startTimePeriodLongSP = getSP.getLong(argsStartTimePeriodKey)
        endTimePeriodLongSP = getSP.getLong(argsEndTimePeriodKey)
    }

    private fun selectedCategoriesSetKey(): String {
        val reportTypeKey = paymentTypeId?.toString() ?: "all"
        return "${Constants.FOR_REPORTS_SELECTED_CATEGORIES_LIST_KEY}_$reportTypeKey"
    }

    private suspend fun getListOfFullMoneyMovements(query: SimpleSQLiteQuery): List<FullMoneyMoving>? {
//        Message.log("query = ${query.sql}, args = ${query.argCount}")

        val result = MoneyMovingUseCase.getSelectedMoneyMovement(
            db, query
        )
        return result
    }

    suspend fun updateReports(await: Boolean) {
        runBlocking {
            getTimePeriodsSP()
            setTextOnButtons()
            setReportTexts()
            getNumbersOfAllCategories()
            getCategoriesSet()
            updateCategoriesFilterText()
            val query = createQuery()

            listFullMoneyMoving = async(Dispatchers.IO) { getListOfFullMoneyMovements(query) }

//            Message.log(
//                "result of get List fulMoneyMoving ${
//                    listFullMoneyMoving.await()?.joinToString()
//                }"
//            )

//            val listMoneyMovingForReports: Deferred<List<FullMoneyMoving>?> =
//                async(Dispatchers.IO) { getListOfFullMoneyMovements(query) }
            val movements = listFullMoneyMoving.await().orEmpty()
            val items = ConvToList.moneyMovementListToReportCategoryItems(movements)
            updateSummary(movements, items)
            _reportCategoryItems.postValue(items)
            _isEmpty.postValue(items.isEmpty())
            if (items.isEmpty()) {
                _totalAmountText.postValue(formatAmount(0.0, null))
            }
        }
    }

    private fun createQuery(): SimpleSQLiteQuery {
        return ReportsCreateSimpleQuery.createSampleQueryForReports(
            startTimePeriodLong = startTimePeriodLongSP,
            endTimePeriodLong = endTimePeriodLongSP,
            paymentTypeId = paymentTypeId,
            setItemsOfCategories = selectedCategoriesSet,
            numbersOfAllCategories = numbersOfAllCategories,
            languageTag = AppLanguage.getSelectedTag(app.applicationContext)
        )
    }

    suspend fun getNumbersOfCategories(): Int {
        return CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory).size
    }

    suspend fun getListOfCategories(): List<Categories> {
        return CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategory)
    }

    fun updateSelectedCategories(categoriesSet: Set<Int>): Boolean {
        selectedCategoriesSet = categoriesSet
        return true
    }

    private fun updateSummary(
        movements: List<FullMoneyMoving>,
        items: List<ReportCategoryItem>
    ) {
        val totalAmount = items.sumOf { it.amount }
        val currencyShortName = movements.firstOrNull()?.currencyShortNameValue
        _currencyShortName.postValue(currencyShortName)
        _totalAmountText.postValue(formatAmount(totalAmount, currencyShortName))
        _summaryDetailsText.postValue(
            app.getString(
                com.chico.myhomebookkeeping.R.string.report_summary_details,
                movements.size,
                items.size
            )
        )
    }

    private fun updateCategoriesFilterText() {
        val selectedCount = selectedCategoriesSet.size
        val text = if (selectedCount == numbersOfAllCategories) {
            app.getString(com.chico.myhomebookkeeping.R.string.report_filter_all_categories)
        } else {
            app.getString(com.chico.myhomebookkeeping.R.string.report_filter_selected_categories, selectedCount)
        }
        _categoriesFilterText.postValue(text)
    }

    private fun createPeriodText(): String {
        val allTime = app.getString(com.chico.myhomebookkeeping.R.string.text_on_button_time_period_all_time)
        val from = app.getString(com.chico.myhomebookkeeping.R.string.text_on_button_time_period_from)
        val to = app.getString(com.chico.myhomebookkeeping.R.string.text_on_button_time_period_to)
        return when {
            startTimePeriodLongSP > 0 && endTimePeriodLongSP > 0 ->
                "${startTimePeriodLongSP.parseTimeFromMillisShortDate()} - ${endTimePeriodLongSP.parseTimeFromMillisShortDate()}"
            startTimePeriodLongSP > 0 ->
                "$from ${startTimePeriodLongSP.parseTimeFromMillisShortDate()}"
            endTimePeriodLongSP > 0 ->
                "$to ${endTimePeriodLongSP.parseTimeFromMillisShortDate()}"
            else -> allTime
        }
    }

    private fun formatAmount(amount: Double, currencyShortName: String?): String {
        val formatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.getDefault()))
        val formattedAmount = formatter.format(amount)
        return if (currencyShortName.isNullOrBlank()) {
            formattedAmount
        } else {
            "$formattedAmount $currencyShortName"
        }
    }
}
