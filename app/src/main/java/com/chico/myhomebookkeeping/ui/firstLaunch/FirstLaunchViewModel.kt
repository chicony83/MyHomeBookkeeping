package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.*

class FirstLaunchViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCategories: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbFastPayments: FastPaymentsDao =
        dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)

    private val uiHelper = UiHelper()

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }

    fun addFirstLaunchElements(
        listCashAccounts: List<CheckBox>,
        listCurrencies: List<CheckBox>,
        listIncomeCategories: List<CheckBox>,
        listSpendingCategories: List<CheckBox>
    ) = runBlocking {
        val resultAddedIncomeCategories =
            async(Dispatchers.IO) { addIncomeCategories(listIncomeCategories) }
        val resultAddSpendingCategories =
            async(Dispatchers.IO) { addSpendingCategories(listSpendingCategories) }

        val resultAddCashAccount = async(Dispatchers.IO) { addCashAccounts(listCashAccounts) }
        val resultAddCurrencies = async(Dispatchers.IO) { addCurrencies(listCurrencies) }

        val sizeCategoriesList: Int = listIncomeCategories.size + listSpendingCategories.size

        launchIo {
            while (getCategoriesList().size < sizeCategoriesList) {
                delay(100)
                addFreeFastPayments()
            }
        }
    }

    private suspend fun addFreeFastPayments() {
        Message.log("create payment")
        launchIo {
            val categoriesList = CategoriesUseCase.getAllCategoriesSortIdAsc(db = dbCategories)
            for (i in categoriesList.indices) {
                FastPaymentsUseCase.addNewFastPayment(
                    db = dbFastPayments,
                    FastPayments(
                        null,
                        categoriesList[i].categoryName,
                        0,
                        1,
                        1,
                        categoriesList[i].categoriesId ?: 0,
                        null,
                        null
                    )
                )
            }
        }
    }

    private suspend fun getCategoriesList(

    ): List<Categories> {

        return CategoriesUseCase.getAllCategoriesSortIdAsc(dbCategories)
    }

    private fun addSpendingCategories(listSpendingCategories: List<CheckBox>): Long {
        var result: Long = 0
        launchIo {
            for (i in listSpendingCategories.indices) {
                result += addCategory(listSpendingCategories[i].text.toString(), false)
            }
        }
        return result
    }

    private fun addIncomeCategories(listIncomeCategories: List<CheckBox>): Long {
        var result: Long = 0
        launchIo {
            for (i in listIncomeCategories.indices) {
                result += addCategory(listIncomeCategories[i].text.toString(), true)
            }
        }
        return result
    }

    private suspend fun addCategory(name: String, isIncome: Boolean): Long {
        return dbCategories.addCategory(Categories(name, isIncome))
    }

    private fun addCurrencies(listCurrencies: List<CheckBox>): Boolean {
        for (i in listCurrencies.indices) {
            if (uiHelper.isCheckedCheckBox(listCurrencies[i])) {
                addCurrency(listCurrencies[i].text.toString())
            }
        }
        return true
    }

    private fun addCurrency(name: String) {
        val currency = Currencies(
            currencyName = name
        )
        launchIo {
            dbCurrencies.addCurrency(currency)
        }
    }

    private fun addCashAccounts(listCashAccounts: List<CheckBox>): Boolean {
        for (i in listCashAccounts.indices) {
            if (uiHelper.isCheckedCheckBox(listCashAccounts[i])) {
                addCashAccount(listCashAccounts[i].text.toString())
            }
        }
        return true
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

}