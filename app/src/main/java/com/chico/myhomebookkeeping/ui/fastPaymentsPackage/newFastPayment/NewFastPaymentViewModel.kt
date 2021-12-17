package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class NewFastPaymentViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME

    private val argsDescriptionFastPaymentKey =
        Constants.FOR_NEW_FAST_PAYMENTS_DESCRIPTION_FAST_PAYMENT
    private val argsRatingKey = Constants.FOR_NEW_FAST_PAYMENT_RATING
    private val argsCashAccount = Constants.FOR_NEW_FAST_PAYMENT_CASH_ACCOUNT
    private val argsCurrency = Constants.FOR_NEW_FAST_PAYMENT_CURRENCY
    private val argsCategory = Constants.FOR_NEW_FAST_PAYMENT_CATEGORY
    private val argsAmount = Constants.FOR_NEW_FAST_PAYMENT_AMOUNT
    private val argsDescription = Constants.FOR_NEW_FAST_PAYMENT_DESCRIPTION_OF_PAYMENT

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

    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
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

    private val _rating = MutableLiveData<Int>()
    val rating: LiveData<Int> get() = _rating

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

    init {
        getSPValues()
        setValuesViewModel()
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
        launchIo { if (modelCheck.isPositiveValue(ratingSPInt)) launchUi { postRating() } }
        launchIo { if (modelCheck.isPositiveValue(cashAccountSPInt)) launchUi { postCashAccount() } }
        launchIo { if (modelCheck.isPositiveValue(currencySPInt)) launchUi { postCurrency() } }
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

    private suspend fun postCurrency() {
        _currency.postValue(CurrenciesUseCase.getOneCurrency(dbCurrencies, currencySPInt))
    }

    private suspend fun postCashAccount() {
        _cashAccount.postValue(
            CashAccountsUseCase.getOneCashAccount(
                dbCashAccount,
                cashAccountSPInt
            )
        )
    }

    private fun postRating() {
        _rating.postValue(ratingSPInt)
    }

    fun saveDataToSP(
        descriptionFastPayment: String,
        description: String,
        amount: Double
    ) {
        with(setSP) {
            saveToSP(argsDescriptionFastPaymentKey, descriptionFastPayment)
            saveToSP(argsRatingKey, _rating.value?.toInt())
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

    fun setRating(rating: Int) {
        when (rating) {
            in 0..9 -> postRatingImg(R.drawable.rating1)
            in 10..19 -> postRatingImg(R.drawable.rating2)
            in 20..29 -> postRatingImg(R.drawable.rating3)
            in 30..39 -> postRatingImg(R.drawable.rating4)
            in 40..50 -> postRatingImg(R.drawable.rating5)
        }
    }

    private fun postRatingImg(ratingImg: Int) {
        _rating.postValue(ratingImg)
    }

    fun isCashAccountNotNull(): Boolean {
        return _cashAccount.value != null
    }

    fun isCurrencyNotNull(): Boolean {
        return _currency.value != null
    }

    fun isCategoryNotNull():Boolean{
        return _category.value != null
    }

    fun addNewFastPayment(
        descriptionFastPayment: String,
        description: String,
        amount: Double
    ): Long {
        return 1L
    }
}