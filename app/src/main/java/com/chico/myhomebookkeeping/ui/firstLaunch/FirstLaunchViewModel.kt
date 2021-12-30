package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchIo

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

    //    private val argsIsFirstLaunch = Constants.IS_FIRST_LAUNCH
    private val uiHelper = UiHelper()

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }

    fun addCashAccountCard(addCashAccountsCard: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCashAccountsCard)) {
            addCashAccount(getString(R.string.quick_setup_name_Card))
        }
    }

    fun addCashAccountCash(addCashAccountsCash: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCashAccountsCash)) {
            addCashAccount(getString(R.string.quick_setup_name_Cash))
        }
    }

    fun addDefaultCurrency(addDefaultCurrency: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addDefaultCurrency)) {
            addCurrency(getString(R.string.quick_setup_name_Currency))
        }
    }

    fun addCategoryTheSalary(addCategoryTheSalary: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCategoryTheSalary)) {
            addCategory(getString(R.string.quick_setup_name_The_Income), true)
        }
    }

    fun addCategoryProducts(addCategoryProducts: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCategoryProducts)) {
            addCategory(getString(R.string.quick_setup_name_Products), false)
        }
    }

    fun addCategoryFuelForTheCar(addCategoryFuelForTheCar: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCategoryFuelForTheCar)) {
            addCategory(getString(R.string.quick_setup_name_Fuel_for_the_car), false)
        }
    }

    fun addCategoryCellularCommunication(addCategoryCellularCommunication: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCategoryCellularCommunication)) {
            addCategory(getString(R.string.quick_setup_name_Cellular_communication),false)
        }
    }

    fun addCategoryCredit(addCategoryCredit: CheckBox) {
        if (uiHelper.isCheckedCheckBox(addCategoryCredit)) {
            addCategory(getString(R.string.quick_setup_name_Credit), false)
        }
    }

    private fun addCashAccount(name: String) {
        val cashAccount = CashAccount(
            accountName = name,
            bankAccountNumber = ""
        )
        launchIo {
            dbCashAccount.addCashAccount(cashAccount)
        }
    }

    private fun addCurrency(name: String) {
        val currency = Currencies(
            currencyName = name
        )
        launchIo {
            dbCurrencies.addCurrency(currency)
        }
    }

    private fun addCategory(name: String, isIncome: Boolean) {
        val category = Categories(
            categoryName = name,
            isIncome = isIncome
        )
        launchIo {
            dbCategories.addCategory(category)
        }
    }


    private fun getString(str: Int) = app.getString(str)

}