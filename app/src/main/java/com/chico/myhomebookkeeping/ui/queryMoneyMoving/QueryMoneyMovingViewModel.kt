package com.chico.myhomebookkeeping.ui.queryMoneyMoving

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase

class QueryMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_QUERY_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsIncomeSpending = Constants.FOR_QUERY_CATEGORIES_INCOME_SPENDING_KEY
    private val argsNone = Constants.FOR_QUERY_NONE

    private val modelCheck = ModelCheck()

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()
    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val sharedPreferences: SharedPreferences = app.getSharedPreferences(
        spName,
        Context.MODE_PRIVATE
    )
    private val spEditor = sharedPreferences.edit()

    private val spValues = GetSP(sharedPreferences)

    private val _selectedCurrency = MutableLiveData<Currencies?>()
    val selectedCurrency: MutableLiveData<Currencies?>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<CashAccount?>()
    val selectedCashAccount: MutableLiveData<CashAccount?>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<Categories?>()
    val selectedCategory: MutableLiveData<Categories?>
        get() = _selectedCategory

    var cashAccountSP = -1
    var currencySP = -1
    var categorySP = -1
    private var incomeSpendingCategories: String = argsNone


//    var cashAccountIdBundle: Int = -1
//    var currenciesIdBundle: Int = -1
//    var categoryIdBundle: Int = -1


    fun checkArguments(arguments: Bundle?) {
        getSharedPreferencesArgs()
//        getBundleArgs(arguments)
//        setValuesViewModel()
    }

//    private fun setValuesViewModel() {
//        launchIo {
//            if (doubleCheck(cashAccountSP, cashAccountIdBundle)) postCashAccount(cashAccountSP)
//
//            if (viewModelCheck.isPositiveValue(cashAccountIdBundle)) postCashAccount(
//                cashAccountIdBundle
//            )
//        }
//        launchIo {
//            if (doubleCheck(currencySP, currenciesIdBundle)) postCurrency(currencySP)
//
//            if (viewModelCheck.isPositiveValue(currenciesIdBundle)) postCurrency(currenciesIdBundle)
//
//        }
//        launchIo {
//            if (doubleCheck(categorySP, cashAccountIdBundle)) postCategory(categorySP)
//
//            if (viewModelCheck.isPositiveValue(categoryIdBundle)) postCategory(categoryIdBundle)
//        }
//    }

//    private fun doubleCheck(checkOne: Int, checkTwo: Int): Boolean {
//        return spValues.isPositiveValue(checkOne) and !spValues.isPositiveValue(checkTwo)
//    }

//    private suspend fun postCategory(idNum: Int) {
//        _selectedCategory.postValue(
//            CategoriesUseCase.getOneCategory(dbCategory, idNum)
//        )
//    }

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


//    private fun getBundleArgs(arguments: Bundle?) {
//        cashAccountIdBundle = viewModelCheck.getValueBundle(arguments, argsCashAccountKey)
//        currenciesIdBundle = viewModelCheck.getValueBundle(arguments, argsCurrencyKey)
//        categoryIdBundle = viewModelCheck.getValueBundle(arguments, argsCategoryKey)
//    }

    private fun getSharedPreferencesArgs() {
        cashAccountSP = spValues.getInt(argsCashAccountKey)
        currencySP = spValues.getInt(argsCurrencyKey)
        categorySP = spValues.getInt(argsCategoryKey)
        incomeSpendingCategories = spValues.getString(argsIncomeSpending)?:argsNone
    }

    fun reset() {
        _selectedCashAccount.postValue(null)
        _selectedCategory.postValue(null)
        _selectedCurrency.postValue(null)
    }

    fun saveData() {
        spEditor.putInt(argsCurrencyKey, _selectedCurrency.value?.currencyId ?: -1)
        spEditor.putInt(argsCashAccountKey, _selectedCashAccount.value?.cashAccountId ?: -1)
        spEditor.putInt(argsCategoryKey, _selectedCategory.value?.categoriesId ?: -1)
        spEditor.putString(argsIncomeSpending,incomeSpendingCategories)

        spEditor.commit()
    }

}
