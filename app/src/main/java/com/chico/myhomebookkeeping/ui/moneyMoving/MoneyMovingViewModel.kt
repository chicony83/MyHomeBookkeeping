package com.chico.myhomebookkeeping.ui.moneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.checks.ViewModelCheck
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_QUERY_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_QUERY_CATEGORY_KEY

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()


    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()

    private var viewModelCheck = ViewModelCheck(sharedPreferences)

    private val _moneyMovementList = MutableLiveData<List<FullMoneyMoving>>()
    val moneyMovementList: LiveData<List<FullMoneyMoving>>
        get() = _moneyMovementList

    private val _textDescriptionOfQueryCurrency = MutableLiveData<String>()
    val textDescriptionOfQueryCurrency: LiveData<String>
        get() = _textDescriptionOfQueryCurrency

    private val _textDescriptionOfQueryCategory = MutableLiveData<String>()
    val textDescriptionOfQueryCategory: LiveData<String>
        get() = _textDescriptionOfQueryCategory

    private val _textDescriptionOfQueryCashAccount = MutableLiveData<String>()
    val textDescriptionOfQueryCashAccount: LiveData<String>
        get() = _textDescriptionOfQueryCashAccount

    private var cashAccountSP = -1
    private var currencySP: Int = -1
    private var categorySP = -1

    init {
        getValuesSP()
        loadMoneyMovement()
    }

    private fun getValuesSP() {
        cashAccountSP = viewModelCheck.getValueSP(argsCashAccountKey)
        currencySP = viewModelCheck.getValueSP(argsCurrencyKey)
        categorySP = viewModelCheck.getValueSP(argsCategoryKey)
    }

    private fun loadMoneyMovement() {

        if (viewModelCheck.isPositiveValue(currencySP)) {
            launchUi {
                _textDescriptionOfQueryCurrency.postValue(
                    CurrenciesUseCase.getOneCurrency(
                        dbCurrencies,
                        currencySP
                    )?.currencyName
                )
            }
        }
        if (viewModelCheck.isPositiveValue(categorySP)) {
            launchUi {
                _textDescriptionOfQueryCategory.postValue(
                    CategoriesUseCase.getOneCategory(
                        dbCategory,
                        categorySP
                    )?.categoryName
                )
            }
        }
        if (viewModelCheck.isPositiveValue(cashAccountSP)) {
            launchUi {
                _textDescriptionOfQueryCashAccount.postValue(
                    CashAccountsUseCase.getOneCashAccount(
                        dbCashAccount,
                        cashAccountSP
                    )?.accountName
                )
            }
        }

        runBlocking {
            val moneyMovingCreteQuery = MoneyMovingCreteQuery()
            val query = moneyMovingCreteQuery.createQuery(
                    currencySP,
                    categorySP,
                    cashAccountSP
                )

        _moneyMovementList.postValue(MoneyMovingUseCase.getSelectedMoneyMovement(db, query))
        }
    }

}