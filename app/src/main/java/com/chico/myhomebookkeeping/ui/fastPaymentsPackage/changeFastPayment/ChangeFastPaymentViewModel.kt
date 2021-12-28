package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.data.newFastPayment.Rating
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ChangeFastPaymentViewModel(
    val app: Application
) : AndroidViewModel(app) {

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

    private val _paymentCashAccount = MutableLiveData<CashAccount>()
    val paymentCashAccount: LiveData<CashAccount> get() = _paymentCashAccount

    private val _paymentCategory = MutableLiveData<Categories>()
    val paymentCategory: LiveData<Categories> get() = _paymentCategory

    private val _paymentAmount = MutableLiveData<String?>()
    val paymentAmount: LiveData<String?> get() = _paymentAmount

    private val _paymentDescription = MutableLiveData<String?>()
    val paymentDescription: LiveData<String?> get() = _paymentDescription

    private var idFastMoneyMovingForChange: Long = minusOneLong

    fun getFastPaymentForChange() {
        idFastMoneyMovingForChange = getIDFastPaymentForChange()
        if (modelCheck.isPositiveValue(idFastMoneyMovingForChange)) {
            launchIo {
                getFastPayment()
            }
        }
    }

    private fun getIDFastPaymentForChange(): Long {
        return getSP.getLong(argsIdFastPaymentForChangeKey)
    }

    private suspend fun getFastPayment() = coroutineScope {
        val fastPayment = async {
            FastPaymentsUseCase.getOneFastPayment(idFastMoneyMovingForChange, dbFastPayments)
        }
        postName(fastPayment.await())
        postRating(fastPayment.await())
        postCashAccount(fastPayment.await())
        postCurrency(fastPayment.await())
        postCategory(fastPayment.await())
        postAmount(fastPayment.await())
        postDescription(fastPayment.await())
    }

    private fun postDescription(fastPayments: FastPayments?) {
        _paymentDescription.postValue(getPostingDescription(fastPayments))
    }

    private fun getPostingDescription(fastPayments: FastPayments?): String {
        return if (modelCheck.isPositiveValue(descriptionSPString)) {
            descriptionSPString
        } else {
            fastPayments?.description ?: textEmpty
        }
    }

    private fun postAmount(fastPayments: FastPayments?) {
        _paymentAmount.postValue(getPostingAmount(fastPayments).toString())
    }

    private fun getPostingAmount(fastPayments: FastPayments?): Double {
        return if (modelCheck.isPositiveValue(amountSPDouble)) {
            amountSPDouble
        } else {
            fastPayments?.amount ?: 0.0
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

    private suspend fun postCurrency(fastPayments: FastPayments?) {
        val postingId: Int = if (modelCheck.isPositiveValue(currencySPInt)) {
            currencySPInt
        } else {
            fastPayments?.currencyId ?: 0
        }
        _paymentCurrency.postValue(CurrenciesUseCase.getOneCurrency(dbCurrencies, postingId))
    }

    private suspend fun postCashAccount(fastPayments: FastPayments?) {
        val postingId: Int = if (modelCheck.isPositiveValue(cashAccountSPInt)) {
            cashAccountSPInt
        } else {
            fastPayments?.cashAccountId ?: 0
        }
        _paymentCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccount(
                dbCashAccount,
                postingId
            )
        )
    }

    private fun postRating(fastPayments: FastPayments?) {
//        val postingRating = if (modelCheck.isPositiveValue(ratingSPInt)) {
//            ratingSPInt
//        } else {
//            fastPayments?.rating
//        }
//        Message.log("rating = $")
        _paymentRating.postValue(getPostingRating(fastPayments))
    }

    private fun getPostingRating(fastPayments: FastPayments?): Rating {
        return if (modelCheck.isPositiveValue(ratingSPInt)){
            Rating(ratingSPInt,getRatingImage(ratingSPInt))
        }
        else{
            val zeroRating = fastPayments?.rating?:0
            Rating(zeroRating,getRatingImage(zeroRating))
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
}