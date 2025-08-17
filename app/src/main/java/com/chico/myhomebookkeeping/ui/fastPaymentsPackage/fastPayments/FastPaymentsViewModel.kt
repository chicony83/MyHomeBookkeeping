package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
//import com.chico.myhomebookkeeping.BuildConfig
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.db.simpleQuery.FastPaymentCreateSimpleQuery
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.enums.SortingFastPayments
import com.chico.myhomebookkeeping.enums.StateRecyclerFastPaymentByType
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.*

class FastPaymentsViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val argsIdFastPaymentForChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_ID
    private val argsCashAccountCreateKey = Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY
    private val argsCurrencyCreateKey = Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY
    private val argsCategoryCreateKey = Constants.ARGS_NEW_PAYMENT_CATEGORY_KEY
    private val argsAmountCreateKey = Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY
    private val argsDescriptionCreateKey = Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY

    private val argsSortingFastPayments = Constants.SORTING_FAST_PAYMENTS
    private val argsStateRecyclerFastPaymentByType = Constants.ARGS_GET_FAST_PAYMENTS_BY_TYPE

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val textEmpty = Constants.TEXT_EMPTY

    private val getSp = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor = sharedPreferences.edit())

    private val db: FastPaymentsDao = dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val _fastPaymentsList = MutableLiveData<List<FullFastPayment>?>()
    val fastPaymentsList: MutableLiveData<List<FullFastPayment>?> get() = _fastPaymentsList

    private var typeOfFastPayments = getStateRecyclerFastPaymentByTypeFromSp()

    internal fun getFullFastPaymentsList() {
        typeOfFastPayments = getStateRecyclerFastPaymentByTypeFromSp()
        runBlocking {
            val listFullFastPayments: Deferred<List<FullFastPayment>?> =
                async(Dispatchers.IO) { loadListFullFastPayments() }
            Message.log("--- size of list full fast payments = ${listFullFastPayments.await()?.size}")
            val result =
                selectTargetingTypeOfFastPayment(listFullFastPayments.await(), typeOfFastPayments)
            postListFullFastPayments(result)
        }
    }

    private fun selectTargetingTypeOfFastPayment(
        list: List<FullFastPayment>?,
        typeOfFastPayments: String
    ): List<FullFastPayment> {
        var resultList = mutableListOf<FullFastPayment>()
        val size: Int = list?.size?.minus(1) ?: 0
        Message.log("size of full list ${list?.size}")
        if (size > 0) {
            when (typeOfFastPayments) {
                StateRecyclerFastPaymentByType.Income.name -> getTargetingLines(
                    list,
                    resultList,
                    true
                )
                StateRecyclerFastPaymentByType.Spending.name -> getTargetingLines(
                    list,
                    resultList,
                    false
                )
                StateRecyclerFastPaymentByType.All.name -> resultList = list?.toMutableList()!!
                else -> resultList = list?.toMutableList()!!
            }

            Message.log("size of resulted list ${resultList.size}")
        }
        return resultList
    }

    private fun getTargetingLines(
        list: List<FullFastPayment>?,
        resultList: MutableList<FullFastPayment>,
        boolean: Boolean
    ): MutableList<FullFastPayment> {
        list?.forEach { it ->
            if (it.isIncome == boolean) {
                resultList.add(it)
                Message.log("add")
            }
        }
        return resultList
    }

    private fun getStateRecyclerFastPaymentByTypeFromSp(): String {
        return getSp.getString(argsStateRecyclerFastPaymentByType)
            ?: StateRecyclerFastPaymentByType.All.name
    }

    private fun postListFullFastPayments(list: List<FullFastPayment>?) {
        _fastPaymentsList.postValue(list)
    }

    private suspend fun loadListFullFastPayments() = launchForResult {

        val query: SimpleSQLiteQuery

        when (getSorting()) {
            SortingFastPayments.AlphabetByAsc.toString() -> {
                query =
                    FastPaymentCreateSimpleQuery.createQuerySortingAlphabetByAsc()
            }
            SortingFastPayments.AlphabetByDesc.toString() -> {
                query =
                    FastPaymentCreateSimpleQuery.createQuerySortingAlphabetByDesc()
            }
            SortingFastPayments.RatingByAsc.toString() -> {
                query =
                    FastPaymentCreateSimpleQuery.createQuerySortingRatingByAsc()
            }
            SortingFastPayments.RatingByDesc.toString() -> {
                query =
                    FastPaymentCreateSimpleQuery.createQuerySortingRatingByDesc()
            }
            else -> {
                query =
                    FastPaymentCreateSimpleQuery.createQuerySortingRatingByDesc()
            }
        }
        Message.log(query.sql)
        return@launchForResult getListFullFastPaymentsFromUseCase(query)
    }

    private fun getString(string: Int) = app.getString(string)

    private fun getSorting(): String? {
        return getSp.getString(argsSortingFastPayments)
    }

    private suspend fun getListFullFastPaymentsFromUseCase(query: SimpleSQLiteQuery): List<FullFastPayment>? {
        return FastPaymentsUseCase.getListFullFastPayments(
            db, query
        )
    }

    suspend fun loadSelectedFullFastPayment(id: Long): FullFastPayment {
        return FastPaymentsUseCase.getOneFullFastPayment(
            db,
            FastPaymentCreateSimpleQuery.createQueryOneFullFastPayment(id)
        )
    }

    fun saveARGSForPay(fastPayment: FullFastPayment?) {
        runBlocking {
            val id = fastPayment?.id ?: 0
            val selectedFastPayment: Deferred<FastPayments?> = async(Dispatchers.IO) {
                FastPaymentsUseCase.getOneFastPayment(id, db)
            }
            saveToSp(selectedFastPayment.await())
        }
    }

    private fun saveToSp(fastPayments: FastPayments?) {
        with(setSP) {
            saveToSP(argsCashAccountCreateKey, fastPayments?.cashAccountId)
            saveToSP(argsCurrencyCreateKey, fastPayments?.currencyId)
            saveToSP(argsCategoryCreateKey, fastPayments?.categoryId)
            saveToSP(argsAmountCreateKey, fastPayments?.amount.toString())
            saveToSP(argsDescriptionCreateKey, fastPayments?.description)
        }
    }

    fun saveIdFastPaymentForChange(id: Long) {
        setSP.saveToSP(argsIdFastPaymentForChangeKey, id)
    }

    fun cleaningSP() {
        with(setSP) {
            val argsId = Constants.ARGS_CHANGE_FAST_PAYMENT_ID
            val argsName = Constants.ARGS_CHANGE_FAST_PAYMENT_NAME
            val argsRating = Constants.ARGS_CHANGE_FAST_PAYMENT_RATING
            val argsCashAccount = Constants.ARGS_CHANGE_FAST_PAYMENT_CASH_ACCOUNT
            val argsCurrency = Constants.ARGS_CHANGE_FAST_PAYMENT_CURRENCY
            val argsCategory = Constants.ARGS_CHANGE_FAST_PAYMENT_CATEGORY
            val argsAmount = Constants.ARGS_CHANGE_FAST_PAYMENT_AMOUNT
            val argsDescription = Constants.ARGS_CHANGE_FAST_PAYMENT_DESCRIPTION

            saveToSP(argsId, minusOneLong)
            saveToSP(argsName, textEmpty)
            saveToSP(argsRating, minusOneInt)
            saveToSP(argsCashAccount, minusOneInt)
            saveToSP(argsCurrency, minusOneInt)
            saveToSP(argsCategory, minusOneInt)
            saveToSP(argsAmount, textEmpty)
            saveToSP(argsDescription, textEmpty)
        }
    }

    fun reloadRecycler() {
        launchIo {
            delay(1000)
            getFullFastPaymentsList()
        }
    }

    fun setSortingCategories(sorting: String) {
        setSP.saveToSP(argsSortingFastPayments, sorting)
    }

    fun isLastVersionOfProgramChecked(): Boolean {
        val lastCheckedVersion = getSp.getInt(ConstantsOfUpdate.LAST_CHECKED_VERSION)
//        val currentVersion = BuildConfig.VERSION_CODE
//        return lastCheckedVersion == currentVersion
        return true
    }

    fun setLastVersionChecked() {
//        setSP.saveToSP(ConstantsOfUpdate.LAST_CHECKED_VERSION, BuildConfig.VERSION_CODE)
    }

    fun getAllFastPayments() {
        setSP.saveToSP(argsStateRecyclerFastPaymentByType, StateRecyclerFastPaymentByType.All.name)
        getFullFastPaymentsList()
    }

    fun getIncomeFastPayments() {
        setSP.saveToSP(
            argsStateRecyclerFastPaymentByType,
            StateRecyclerFastPaymentByType.Income.name
        )
        getFullFastPaymentsList()
    }

    fun getSpendingFastPayments() {
        setSP.saveToSP(
            argsStateRecyclerFastPaymentByType,
            StateRecyclerFastPaymentByType.Spending.name
        )
        getFullFastPaymentsList()
    }

    fun getTypeOfRecycler(): String {
        return typeOfFastPayments
    }

    fun saveIdFastPaymentForPay(id: Long) {
        runBlocking {
            val selectedFastPayment: Deferred<FastPayments?> = async(Dispatchers.IO) {
                FastPaymentsUseCase.getOneFastPayment(id, db)
            }
            saveToSp(selectedFastPayment.await())
        }
    }
}