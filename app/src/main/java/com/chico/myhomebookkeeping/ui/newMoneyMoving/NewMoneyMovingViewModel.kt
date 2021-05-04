package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.NewMoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val argsCashAccountKey = Constants.CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.CURRENCY_KEY
    private val argsCategoryKey = Constants.CATEGORY_KEY
    private val spName = Constants.SP_NAME

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()
    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<CashAccount>()
    val selectedCashAccount: LiveData<CashAccount>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<Categories>()
    val selectedCategory: LiveData<Categories>
        get() = _selectedCategory

    var cashAccountSP = -1
    var currencySP = -1
    var categorySP = -1

    var cashAccountIdBundle: Int = -1
    var currenciesIdBundle: Int = -1
    var categoryIdBundle: Int = -1

    fun checkArguments(arguments: Bundle?) {
        getValuesSP()
        getValuesBundle(arguments)
        setValuesViewModel()
    }

    private fun getValuesBundle(arguments: Bundle?) {
        cashAccountIdBundle = arguments?.getInt(argsCashAccountKey) ?: -1
        currenciesIdBundle = arguments?.getInt(argsCurrencyKey) ?: -1
        categoryIdBundle = arguments?.getInt(argsCategoryKey) ?: -1
    }

    private fun setValuesViewModel() {
        launchIo {
            if (doubleCheck(cashAccountSP, cashAccountIdBundle)) postCashAccount(cashAccountSP)

            if (isPositiveValue(cashAccountIdBundle)) postCashAccount(cashAccountIdBundle)

        }
        launchIo {
            if (doubleCheck(currencySP, currenciesIdBundle)) postCurrency(currencySP)

            if (isPositiveValue(currenciesIdBundle)) postCurrency(currenciesIdBundle)

        }
        launchIo {
            if (doubleCheck(categorySP, cashAccountIdBundle)) postCategory(categorySP)

            if (isPositiveValue(categoryIdBundle)) postCategory(categoryIdBundle)

        }
    }


    private fun doubleCheck(checkOne: Int, checkTwo: Int): Boolean {
        return isPositiveValue(checkOne) and !isPositiveValue(checkTwo)
    }

    private suspend fun postCategory(idNum: Int) {
        _selectedCategory.postValue(
            CategoriesUseCase.getOneCategory(dbCategory, idNum)
        )
    }

    private suspend fun postCurrency(idNum: Int) {
        _selectedCurrency.postValue(
            CurrenciesUseCase.getOneCurrency(dbCurrencies, idNum)
        )
    }

    private suspend fun postCashAccount(idNum: Int) {
        _selectedCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccount(dbCashAccount, idNum)
        )
    }

    private fun getValuesSP() {
        cashAccountSP = checkOneSP(argsCashAccountKey)
        currencySP = checkOneSP(argsCurrencyKey)
        categorySP = checkOneSP(argsCategoryKey)
    }

    private fun isPositiveValue(checkTemp: Int): Boolean {
        return checkTemp > 0
    }

    private fun checkOneSP(argsKey: String): Int {
        return sharedPreferences.getInt(argsKey, -1)
    }

    fun saveData() {
        spEditor.putInt(argsCurrencyKey, _selectedCurrency.value?.id ?: -1)
        spEditor.putInt(argsCashAccountKey, _selectedCashAccount.value?.id ?: -1)
        spEditor.putInt(argsCategoryKey, _selectedCategory.value?.id ?: -1)

        spEditor.commit()
    }

    suspend fun addNewMoneyMoving(
        dataTime: Long,
        amount: Double,
        description: String
    ): Long {
        val cashAccountValue: Int = _selectedCashAccount.value?.id ?: 0
        val categoryValue: Int = _selectedCategory.value?.id ?: 0
        val currencyValue: Int = _selectedCurrency.value?.id ?: 0
        val moneyMovement = MoneyMovement(
            timeStamp = dataTime,
            amount = amount,
            cashAccount = cashAccountValue,
            category = categoryValue,
            currency = currencyValue,
            description = description
        )
        return NewMoneyMovingUseCase.addInDataBase(dbMoneyMovement, moneyMovement)
    }
}