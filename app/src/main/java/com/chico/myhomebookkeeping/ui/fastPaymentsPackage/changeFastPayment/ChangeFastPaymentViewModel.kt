package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP

class ChangeFastPaymentViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val argsIdChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_ID
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

    private val spValues = GetSP(sharedPreferences)
    private val saveARGS = SetSP(spEditor = sharedPreferences.edit())

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

    private val _paymentRating = MutableLiveData<Int>()
    val paymentRating: LiveData<Int> get() = _paymentRating

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

}