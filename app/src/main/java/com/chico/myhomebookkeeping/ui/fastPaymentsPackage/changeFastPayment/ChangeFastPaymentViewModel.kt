package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.data.newFastPayment.Rating
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.domain.*
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.*

class ChangeFastPaymentViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val currenciesDao: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val cashAccountDao: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val userParentCategoriesDao: UserParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).userParentCategoriesDao()
    private val fastPaymentsDao: FastPaymentsDao =
        dataBase.getDataBase(app.applicationContext).fastPaymentsDao()
    private val userParentDao: UserParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).userParentCategoriesDao()

    private val argsIdFastPaymentForChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_ID
    private val argsNameChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_NAME
    private val argsRatingChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_RATING
    private val argsCashAccountChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_CASH_ACCOUNT
    private val argsCurrencyChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_CURRENCY
    private val argsCategoryChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_CATEGORY
    private val argsAmountChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_AMOUNT
    private val argsDescriptionChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_DESCRIPTION

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG
    private val textEmpty = Constants.TEXT_EMPTY

    private var nameSPString: String = textEmpty
    private var ratingSPInt: Int = minusOneInt
    private var cashAccountSPInt: Int = minusOneInt
    private var currencySPInt: Int = minusOneInt
    private var categorySPInt: Int = minusOneInt
    private var amountSPDouble: Double = minusOneInt.toDouble()
    private var descriptionSPString: String = textEmpty

    private val spName = Constants.SP_NAME
    private val modelCheck = ModelCheck()
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor = sharedPreferences.edit())

    private val dbFastPayments: FastPaymentsDao =
        dataBase.getDataBase(app.applicationContext).fastPaymentsDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val _paymentName = MutableLiveData<String>()
    val paymentName: LiveData<String> get() = _paymentName

    private val _paymentRating = MutableLiveData<Rating>()
    val paymentRating: LiveData<Rating> get() = _paymentRating

    private val _paymentCurrency = MutableLiveData<Currencies>()
    val paymentCurrency: LiveData<Currencies> get() = _paymentCurrency

    private val _allCashAccounts = MutableLiveData<List<CashAccount>>()
    val allCashAccounts: LiveData<List<CashAccount>> get() = _allCashAccounts

    private val _paymentCashAccount = MutableLiveData<CashAccount>()
    val paymentCashAccount: LiveData<CashAccount> get() = _paymentCashAccount

    private val _paymentCategory = MutableLiveData<Categories>()
    val paymentCategory: LiveData<Categories> get() = _paymentCategory

    private val _paymentAmount = MutableLiveData<String?>()
    val paymentAmount: LiveData<String?> get() = _paymentAmount

    private val _paymentDescription = MutableLiveData<String?>()
    val paymentDescription: LiveData<String?> get() = _paymentDescription

    private var idFastMoneyMovingForChange: Long = minusOneLong

    private val _currenciesList = MutableLiveData<List<Currencies>>()
    val currenciesList: LiveData<List<Currencies>> = _currenciesList

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies> = _selectedCurrency

    private val _loadedCurrencyId = MutableLiveData<Currencies>()
    val loadedCurrencyId: LiveData<Currencies> = _loadedCurrencyId

    private val _parentCategoryName = MutableLiveData<String>()
    val parentCategoryName: LiveData<String> = _parentCategoryName

    private val _userParentCategory = MutableLiveData<UserParentCategory>()
    val userParentCategory: LiveData<UserParentCategory> = _userParentCategory

    private val _childCategoryName = MutableLiveData<String>()
    val childCategoryName: LiveData<String> = _childCategoryName

    init {
        loadCurrencies()
        loadCashAccounts()
    }

    private fun loadCurrencies() {
        launchIo {
            _currenciesList.postValue(currenciesDao.getAllCurrenciesSortNameAsc())
        }
    }

    private fun loadCashAccounts() {
        launchIo {
            _allCashAccounts.postValue(cashAccountDao.getAllCashAccountsSortIdAsc())
        }
    }

    fun setFastPaymentForChange(fullFastPayment: FullFastPayment?) {
        _paymentName.value = fullFastPayment?.nameFastPayment

        postRating(fullFastPayment?.rating)
        if (_paymentCashAccount.value == null) {
            _paymentCashAccount.value =
                allCashAccounts.value?.firstOrNull { it.accountName.lowercase() == fullFastPayment?.cashAccountNameValue?.lowercase() }
        }
        _loadedCurrencyId.value =
            currenciesList.value?.firstOrNull { it.currencyName == fullFastPayment?.currencyNameValue }
        postAmount(fullFastPayment)
        _paymentDescription.value = fullFastPayment?.description.orEmpty()
        _parentCategoryName.value = fullFastPayment?.categoryNameValue.orEmpty()
        _childCategoryName.value = fullFastPayment?.childCategories?.getOrNull(0)?.name.orEmpty()
        viewModelScope.launch {
            _userParentCategory.postValue(
                userParentCategoriesDao.getAllUserParentCategoriesSortNameASC()
                    .firstOrNull { it.name == fullFastPayment?.categoryNameValue.orEmpty() })
        }
    }

    fun setSelectedCashAccount(cashAccount: CashAccount) {
        _paymentCashAccount.value = cashAccount
    }

    private fun postAmount(fullFastPayment: FullFastPayment?) {
        _paymentAmount.value = getPostingAmount(fullFastPayment).toString()
    }

    private fun getPostingAmount(fullFastPayment: FullFastPayment?): Double {
        return if (modelCheck.isPositiveValue(amountSPDouble)) {
            amountSPDouble
        } else {
            fullFastPayment?.amount ?: 0.0
        }
    }

    private suspend fun postCategory(fastPayments: FastPayments?) {
        val postingId: Int = if (modelCheck.isPositiveValue(categorySPInt)) {
            categorySPInt
        } else {
            fastPayments?.categoryId ?: 0
        }
        _paymentCategory.postValue(CategoriesUseCase.getOneCategory(dbCategory, postingId))
    }

    fun postCurrency(currencyId: Int?) {
        _selectedCurrency.value = currenciesList.value?.firstOrNull { it.currencyId == currencyId }
        _paymentCurrency.value = currenciesList.value?.firstOrNull { it.currencyId == currencyId }
    }

    private fun postRating(rating: Int?) {
//        val postingRating = if (modelCheck.isPositiveValue(ratingSPInt)) {
//            ratingSPInt
//        } else {
//            fastPayments?.rating
//        }
//        Message.log("rating = $")
        _paymentRating.value = getPostingRating(rating)
    }

    private fun getPostingRating(rating: Int?): Rating {
        return if (modelCheck.isPositiveValue(ratingSPInt)) {
            Rating(ratingSPInt, getRatingImage(ratingSPInt))
        } else {
            val zeroRating = rating ?: 0
            Rating(zeroRating, getRatingImage(zeroRating))
        }
    }

    private fun getRatingImage(rating: Int): Int {
        return when (rating) {
            0 -> R.drawable.rating1
            1 -> R.drawable.rating2
            2 -> R.drawable.rating3
            3 -> R.drawable.rating4
            4 -> R.drawable.rating5
            else -> R.drawable.rating1
        }
    }

    private fun postName(fastPayments: FastPayments?) {
        _paymentName.postValue(getPostingName(fastPayments))
    }

    private fun getPostingName(fastPayments: FastPayments?): String {
        return if (modelCheck.isPositiveValue(nameSPString)) {
            nameSPString
        } else {
            fastPayments?.nameFastPayment ?: textEmpty
        }
    }

    fun postRatingValue(value: Int) {
        _paymentRating.postValue(Rating(value, getRatingImage(value)))
    }

    fun saveDataToSP(name: String, amount: String, description: String) {
        with(setSP) {
            saveToSP(argsIdFastPaymentForChangeKey, idFastMoneyMovingForChange)
            saveToSP(argsNameChangeKey, name)
            saveToSP(argsRatingChangeKey, _paymentRating.value?.rating)
            saveToSP(argsCashAccountChangeKey, _paymentCashAccount.value?.cashAccountId)
            saveToSP(argsCurrencyChangeKey, _paymentCurrency.value?.currencyId)
            saveToSP(argsCategoryChangeKey, _paymentCategory.value?.categoriesId)
            saveToSP(argsAmountChangeKey, amount)
            saveToSP(argsDescriptionChangeKey, description)
        }
    }

    fun getSPForChangeFastPayment() {
        nameSPString = getSP.getString(argsNameChangeKey) ?: textEmpty
        ratingSPInt = getSP.getInt(argsRatingChangeKey)
        cashAccountSPInt = getSP.getInt(argsCashAccountChangeKey)
        currencySPInt = getSP.getInt(argsCurrencyChangeKey)
        categorySPInt = getSP.getInt(argsCategoryChangeKey)
        amountSPDouble = getSPAmount()

//        amountSPDouble = getSP.getString(argsAmountChangeKey)?.toDouble() ?: 0.0
        descriptionSPString = getSP.getString(argsDescriptionChangeKey) ?: textEmpty
    }

    private fun getSPAmount(): Double {
        return if (!getSP.getString(argsAmountChangeKey).isNullOrEmpty()) {
            if (getSP.getString(argsAmountChangeKey)!!.isDigitsOnly()) {
                getSP.getString(argsAmountChangeKey)?.toDouble() ?: 0.0
            } else {
                0.0
            }
        } else {
            0.0
        }
//        getSP.getString(argsAmountChangeKey)
    }

    suspend fun changeFastPayment(
        nameFastPayment: String,
        nameMainCategory: String,
        isIncomeCategory: Boolean,
        nameChildCategory: String,
        description: String,
        amount: Double,
        cashAccountId: Int,
        currencyId: Int,
        rating: Int,
    ): Int {

        userParentDao.addNewUserParentCategory(
            UserParentCategory(
                name = nameMainCategory,
                isIncome = isIncomeCategory,
                iconRes = null
            )
        )

        val oldFastPayment = fastPaymentsDao.getAllFastPayments()
            .firstOrNull { it.nameFastPayment == paymentName.value }

        return ChangeFastPaymentUseCase.changeFastPayment(
            db = fastPaymentsDao,
            id = oldFastPayment?.id?:-1,
            amount = amount,
            description = description,
            name = nameFastPayment,
            currencyId = currencyId,
            rating = rating,
            cashAccount = cashAccountId,
            category = oldFastPayment?.categoryId?:0,
            childCategories = listOf(
                ChildCategory(
                    name = nameChildCategory,
                    parentName = nameMainCategory
                )
            )
        )
    }

    suspend fun deleteLine(): Int {
        val oldFastPayment = fastPaymentsDao.getAllFastPayments()
            .firstOrNull { it.nameFastPayment == paymentName.value }
        return ChangeFastPaymentUseCase.deleteLine(dbFastPayments, oldFastPayment?.id?:-1)
    }
}