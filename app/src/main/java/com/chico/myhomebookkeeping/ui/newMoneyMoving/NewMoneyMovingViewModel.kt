package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.NewMoneyMovement
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
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

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()


    fun checkArguments(arguments: Bundle?) {
        val cashAccountId: Int? = arguments?.getInt(argsCashAccountKey)
        if (cashAccountId != null) {
            if (cashAccountId > 0) {
                launchIo {
                    _selectedCashAccount.postValue(
                        CashAccountsUseCase.getOneCashAccount(
                            dbCashAccount,
                            cashAccountId
                        )
                    )
                }
                NewMoneyMovement.cashAccountValue = cashAccountId
            }
        }
        if (NewMoneyMovement.cashAccountValue > 0) {
            launchIo {
                _selectedCashAccount.postValue(
                    CashAccountsUseCase.getOneCashAccount(
                        dbCashAccount,
                        NewMoneyMovement.cashAccountValue
                    )
                )
            }
        }
        val currenciesId: Int? = arguments?.getInt(argsCurrencyKey)
        if (currenciesId != null) {
            if (currenciesId > 0) {
                launchIo {
                    _selectedCurrency.postValue(
                        CurrenciesUseCase.getOneCurrency(
                            dbCurrencies,
                            currenciesId
                        )
                    )
                }
                NewMoneyMovement.currencyValue = currenciesId
            }
        }
        val categoryId: Int? = arguments?.getInt(argsCategoryKey)
        if (categoryId != null) {
            if (categoryId > 0) {
                launchIo {
                    _selectedCategory.postValue(
                        CategoriesUseCase.getOneCategory(
                            dbCategory,
                            categoryId
                        )
                    )
                }
                NewMoneyMovement.categoryValue = categoryId
            }
        }

    }

    private val _selectedCurrency = MutableLiveData<List<Currencies>>()
    val selectedCurrency: LiveData<List<Currencies>>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<List<CashAccount>>()
    val selectedCashAccount: LiveData<List<CashAccount>>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<List<Categories>>()
    val selectedCategory: LiveData<List<Categories>>
        get() = _selectedCategory
}