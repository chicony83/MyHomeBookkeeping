package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
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
import com.chico.myhomebookkeeping.helpers.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class FirstLaunchViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCategories: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)
    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH


    init {
        setTextOnButtons()
    }

    private fun setTextOnButtons() {
        launchUi {

        }
    }

    fun addDefaultCashAccount() {
        val cashAccountCash = CashAccount("Наличные", "")
        val cashAccountCard = CashAccount("Карточка", "")
        launchIo {
            with(CashAccountsUseCase) {
                addNewCashAccount(dbCashAccount, cashAccountCash)
                addNewCashAccount(dbCashAccount, cashAccountCard)
            }
        }
    }

    fun addDefaultCategories() {
        val categoriesSalary = Categories("Зарплата", true)
        val categoriesProducts = Categories("Продукты", false)
        launchIo {
            with(CategoriesUseCase) {
                addNewCategory(dbCategories, categoriesSalary)
                addNewCategory(dbCategories, categoriesProducts)
            }
        }
    }

    fun addDefaultCurrency() {
        val currencies = Currencies("рубли")
        launchIo {
            CurrenciesUseCase.addNewCurrency(dbCurrencies, currencies)
        }
    }

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }
}