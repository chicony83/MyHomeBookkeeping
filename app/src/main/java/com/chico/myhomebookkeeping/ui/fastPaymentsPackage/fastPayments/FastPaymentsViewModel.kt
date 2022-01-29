package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.BuildConfig
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.FullFastPayment
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.db.simpleQuery.FastPaymentCreateSimpleQuery
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.enums.SortingFastPayments
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.obj.Constants
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

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val textEmpty = Constants.TEXT_EMPTY

    private val getSp = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor = sharedPreferences.edit())
//    private val sortingFastPaymentsSpString = getSorting()

    private val db: FastPaymentsDao = dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val setTextOnButtons = SetTextOnButtons(app.resources)

    private var _sortedByTextOnButton = MutableLiveData<String>()
    val sortedByTextOnButton: LiveData<String> get() = _sortedByTextOnButton

    private val _fastPaymentsList = MutableLiveData<List<FullFastPayment>?>()
    val fastPaymentsList: MutableLiveData<List<FullFastPayment>?> get() = _fastPaymentsList

    internal fun getFullFastPaymentsList() {
        runBlocking {
            val listFullFastPayments: Deferred<List<FullFastPayment>?> =
                async(Dispatchers.IO) { loadListFullFastPayments() }
            Message.log("--- size of list full fast payments = ${listFullFastPayments.await()?.size}")
            postListFullFastPayments(listFullFastPayments.await())
        }
    }

    private fun postListFullFastPayments(list: List<FullFastPayment>?) {
        _fastPaymentsList.postValue(list)
    }

    private suspend fun loadListFullFastPayments() = launchForResult {

        val query:SimpleSQLiteQuery
        when (getSorting()) {
            SortingFastPayments.AlphabetByAsc.toString() -> {
                setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_ASC))
                query = FastPaymentCreateSimpleQuery.createQuerySortingAlphabetByAsc()
            }
            SortingFastPayments.AlphabetByDesc.toString() -> {
                setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
                query = FastPaymentCreateSimpleQuery.createQuerySortingAlphabetByDesc()
            }
            SortingFastPayments.RatingByAsc.toString() -> {
                setTextOnButton(getString(R.string.text_on_button_sorting_by_rating_ASC))
                query = FastPaymentCreateSimpleQuery.createQuerySortingRatingByAsc()
            }
            SortingFastPayments.RatingByDesc.toString() -> {
                setTextOnButton(getString(R.string.text_on_button_sorting_by_rating_DESC))
                query = FastPaymentCreateSimpleQuery.createQuerySortingRatingByDesc()
            }
            else -> {
                setTextOnButton(getString(R.string.text_on_button_sorting_by_rating_DESC))
                query = FastPaymentCreateSimpleQuery.createQuerySortingRatingByDesc()
            }
        }
        return@launchForResult getListFullFastPaymentsFromUseCase(query)
    }

    private fun setTextOnButton(string: String) {
        launchUi {
            setTextOnButtons.setTextOnSortingCategoriesButton(_sortedByTextOnButton, string)
        }
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
        val lastCheckedVersion = getSp.getInt(Constants.LAST_CHECKED_VERSION)
        val currentVersion = BuildConfig.VERSION_CODE
        return lastCheckedVersion == currentVersion
    }

    fun setLastVersionChecked() {
        setSP.saveToSP(Constants.LAST_CHECKED_VERSION,BuildConfig.VERSION_CODE)
    }
}