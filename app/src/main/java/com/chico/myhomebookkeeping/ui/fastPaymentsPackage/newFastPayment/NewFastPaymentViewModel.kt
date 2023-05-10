package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.data.newFastPayment.Rating
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.launch

class NewFastPaymentViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val db: CurrenciesDao = dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val argsDescriptionFastPaymentKey =
        Constants.ARGS_NEW_FAST_PAYMENTS_NAME
    private val argsRatingKey = Constants.ARGS_NEW_FAST_PAYMENT_RATING
    private val argsCashAccount = Constants.ARGS_NEW_FAST_PAYMENT_CASH_ACCOUNT
    private val argsCurrency = Constants.ARGS_NEW_FAST_PAYMENT_CURRENCY
    private val argsCategory = Constants.ARGS_NEW_FAST_PAYMENT_CATEGORY
    private val argsAmount = Constants.ARGS_NEW_FAST_PAYMENT_AMOUNT
    private val argsDescription = Constants.ARGS_NEW_FAST_PAYMENT_DESCRIPTION

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val textNone = Constants.TEXT_NONE

    private var descriptionFastPaymentSPString = textNone
    private var ratingSPInt = minusOneInt
    private var cashAccountSPInt = minusOneInt
    private var currencySPInt = minusOneInt
    private var categorySPInt = minusOneInt
    private var amountSPString = textNone
    private var descriptionSPString = textNone

    private val modelCheck = ModelCheck()
    private val dbNewFastPayment: FastPaymentsDao =
        dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    //    private val dbMoneyMovement: MoneyMovementDao =
//        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val setSP = SetSP(spEditor = sharedPreferences.edit())

    private val getSP = GetSP(sharedPreferences)

    private val _descriptionFastPayment = MutableLiveData<String>()
    val descriptionFastPayment: LiveData<String> get() = _descriptionFastPayment

    private val _rating = MutableLiveData<Rating>()
    val rating: LiveData<Rating> get() = _rating

//    private var _ratingVal = 0

    private val _cashAccount = MutableLiveData<CashAccount>()
    val cashAccount: LiveData<CashAccount> get() = _cashAccount

    private val _currency = MutableLiveData<Currencies>()
    val currency: LiveData<Currencies> get() = _currency

    private val _category = MutableLiveData<Categories>()
    val category: LiveData<Categories> get() = _category

    private val _amount = MutableLiveData<Double>()
    val amount: LiveData<Double> get() = _amount

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    private val _currenciesList = MutableLiveData<List<Currencies>>()
    val currenciesList: LiveData<List<Currencies>> = _currenciesList

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies> = _selectedCurrency

    fun loadInitData() {
        getSPValues()
        setValuesViewModel()
        loadCurrencies()
    }

    private fun loadCurrencies() {
        launchIo {
            _currenciesList.postValue(db.getAllCurrenciesSortNameAsc())
        }
    }

    private fun getSPValues() {
        descriptionFastPaymentSPString = getSP.getString(argsDescriptionFastPaymentKey).toString()
        ratingSPInt = getSP.getInt(argsRatingKey)
        cashAccountSPInt = getSP.getInt(argsCashAccount)
        currencySPInt = getSP.getInt(argsCurrency)
        categorySPInt = getSP.getInt(argsCategory)
        amountSPString = getSP.getString(argsAmount).toString()
        descriptionSPString = getSP.getString(argsDescription).toString()
    }

    private fun setValuesViewModel() {
        launchIo { if (descriptionFastPaymentSPString.isNotEmpty()) launchUi { postDescriptionFastPayment() } }
        launchIo {
            if (modelCheck.isPositiveValue(ratingSPInt)) launchUi { postRating() }
            if (!modelCheck.isPositiveValue(ratingSPInt)) launchUi { postRating(0) }
        }
        launchIo { if (modelCheck.isPositiveValue(cashAccountSPInt)) launchUi { postCashAccount() } }
//        launchIo { if (modelCheck.isPositiveValue(currencySPInt)) launchUi { postCurrency() } }
        launchIo { if (modelCheck.isPositiveValue(categorySPInt)) launchUi { postCategory() } }
        launchIo { if (modelCheck.isPositiveValue(amountSPString)) launchUi { postAmount() } }
        launchIo { if (descriptionSPString.isNotEmpty()) launchUi { postDescription() } }
    }

    private fun postDescriptionFastPayment() {
        _descriptionFastPayment.postValue(descriptionFastPaymentSPString)
    }

    private fun postDescription() {
        _description.postValue(descriptionSPString)
    }

    private fun postAmount() {
        _amount.postValue(Around.double(amountSPString))
    }

    private suspend fun postCategory() {
        _category.postValue(CategoriesUseCase.getOneCategory(dbCategory, categorySPInt))

    }

    fun postCurrency(currencyId: Int?) {
        _selectedCurrency.value = currenciesList.value?.firstOrNull { it.currencyId == currencyId }
        _currency.value = currenciesList.value?.firstOrNull { it.currencyId == currencyId }
    }

    private suspend fun postCashAccount() {
        _cashAccount.postValue(
            CashAccountsUseCase.getOneCashAccountById(
                dbCashAccount,
                cashAccountSPInt
            )
        )
    }

    private fun postRating() {
        _rating.postValue(Rating(ratingSPInt, getRatingImg(ratingSPInt)))
    }

    fun postRating(rating: Int) {
        _rating.postValue(Rating(rating, getRatingImg(rating)))
    }

    fun saveDataToSP(
        descriptionFastPayment: String,
        description: String,
        amount: Double
    ) {
        with(setSP) {
            saveToSP(argsDescriptionFastPaymentKey, descriptionFastPayment)
            saveToSP(argsRatingKey, _rating.value?.rating ?: 0)
            saveToSP(argsCashAccount, _cashAccount.value?.cashAccountId)
            saveToSP(argsCurrency, _currency.value?.currencyId)
            saveToSP(argsCategory, _category.value?.categoriesId)
            saveToSP(amountSPString, amount.toString())
            saveToSP(argsDescription, description)
        }
    }

    fun clearSPAfterSave() {
        with(setSP) {
            saveToSP(argsDescriptionFastPaymentKey, textNone)
            saveToSP(argsRatingKey, minusOneInt)
            saveToSP(argsCashAccount, minusOneInt)
            saveToSP(argsCurrency, minusOneInt)
            saveToSP(argsCategory, minusOneInt)
            saveToSP(amountSPString, textNone)
            saveToSP(argsDescription, textNone)
        }
    }

    private fun getRatingImg(rating: Int): Int {
        return when (rating) {
            0 -> R.drawable.rating1
            1 -> R.drawable.rating2
            2 -> R.drawable.rating3
            3 -> R.drawable.rating4
            4 -> R.drawable.rating5
            else -> R.drawable.rating1
        }
    }

    fun isCashAccountNotNull(): Boolean {
        return _cashAccount.value != null
    }

    fun isCurrencyNotNull(): Boolean {
        return _currency.value != null
    }

    fun isCategoryNotNull(): Boolean {
        return _category.value != null
    }

    suspend fun addNewFastPayment(
        nameFastPayment: String,
        description: String,
        amount: Double
    ): Long {

        val newFastPayment = FastPayments(
            icon = null,
            nameFastPayment = nameFastPayment,
            rating = _rating.value?.rating ?: 0,
            cashAccountId = _cashAccount.value?.cashAccountId ?: 0,
            currencyId = _currency.value?.currencyId ?: 0,
            categoryId = _category.value?.categoriesId ?: 0,
            amount = amount,
            description = description
        )
//        Message.log("rating Int value = 0")
//        Message.log("rating newFastPayment = ${newFastPayment.rating.toString()}")

        return FastPaymentsUseCase.addNewFastPayment(
            db = dbNewFastPayment, newFastPayment = newFastPayment
        )
//        return 1L
    }
}