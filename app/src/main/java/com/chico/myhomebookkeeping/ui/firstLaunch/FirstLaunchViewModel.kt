package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.enums.IconCategoriesNames
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
    private val dbIconCategories: IconCategoryDao =
        dataBase.getDataBase(app.applicationContext).iconCategoryDao()
    private val dbIconResources: IconResourcesDao =
        dataBase.getDataBase(app.applicationContext).iconResourcesDao()

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)

    private val uiHelper = UiHelper()

    private val packageName = app.packageName

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
        return dbCategories.addCategory(Categories(name, isIncome, null))
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
            currencyName = name,
            icon = null
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
            bankAccountNumber = "",
            icon = null
        )
        launchIo {
            dbCashAccount.addCashAccount(cashAccount)
        }
    }

    fun addIconCategories() {
        val namesIconCategory = listOf<String>(
            IconCategoriesNames.CashAccounts.name,
            IconCategoriesNames.Categories.name,
            IconCategoriesNames.Currencies.name
        )
        launchIo {
            for (i in namesIconCategory.indices) {
                IconCategoriesUseCase.addIconCategory(
                    dbIconCategories,
                    IconCategory(namesIconCategory[i])
                )
            }
        }
    }

    fun addIconsResources() {
        launchIo {
            var iconCategories = listOf<IconCategory>()
            while (iconCategories.size < 3) {
                delay(100)
                Message.log("--- get icon categories")
                iconCategories = IconCategoriesUseCase.getAllIconCategories(dbIconCategories)
                Message.log("--- size of Icon Categories ${iconCategories.size} ---")
            }
            for (i in iconCategories.indices) {
                when (iconCategories[i].iconCategoryName) {
                    IconCategoriesNames.CashAccounts.name -> addCashAccountsIconsInDB(iconCategories[i])
                    IconCategoriesNames.Categories.name -> addCategoriesIconsInDB(iconCategories[i])
                }
            }
//            addCategoriesIconsInDB(iconCategories)
//            delay(1000)
//            iconCategories = IconCategoriesUseCase.getNumOfAllIconCategories(dbIconCategories)
//            Message.log("---size of Icon Categories $iconCategories ---")
        }
    }

    private suspend fun addCategoriesIconsInDB(iconCategory: IconCategory) {
        Message.log("---Add categories icons---")
        val cashCategoriesIconsList = listOf<Int>(
            getDrawable(R.drawable.category_airplane),
            getDrawable(R.drawable.category_apartment),
            getDrawable(R.drawable.category_arrows_horiz),
            getDrawable(R.drawable.category_build),
            getDrawable(R.drawable.category_bus),
            getDrawable(R.drawable.category_cake),
            getDrawable(R.drawable.category_car),
            getDrawable(R.drawable.category_celebration),
            getDrawable(R.drawable.category_child_friendly),
            getDrawable(R.drawable.category_coffee),
            getDrawable(R.drawable.category_computer),
            getDrawable(R.drawable.category_gas_station),
            getDrawable(R.drawable.category_house),
            getDrawable(R.drawable.category_medical),
            getDrawable(R.drawable.category_park),
            getDrawable(R.drawable.category_pedal_bike),
            getDrawable(R.drawable.category_people),
            getDrawable(R.drawable.category_person),
            getDrawable(R.drawable.category_pets),
            getDrawable(R.drawable.category_phone),
            getDrawable(R.drawable.category_phone_android),
            getDrawable(R.drawable.category_phone_iphone),
            getDrawable(R.drawable.category_restaurant),
            getDrawable(R.drawable.category_salon),
            getDrawable(R.drawable.category_school),
            getDrawable(R.drawable.category_shopping_cart),
            getDrawable(R.drawable.category_shopping_cart_add),
            getDrawable(R.drawable.category_store),
            getDrawable(R.drawable.category_subway),
            getDrawable(R.drawable.category_two_wheeler)
        )
        addIconsRecourseList(
            iconCategory = iconCategory,
            iconsList = cashCategoriesIconsList
        )
    }

    private suspend fun addCashAccountsIconsInDB(iconCategory: IconCategory) {
        Message.log("---Add Cash accounts icons---")
        val cashAccountIconsList = listOf<Int>(
            getDrawable(R.drawable.cash_account_card),
            getDrawable(R.drawable.cash_account_cash),
            getDrawable(R.drawable.cash_account_credit_card_off)
        )
        addIconsRecourseList(cashAccountIconsList, iconCategory)
    }

    private suspend fun addIconsRecourseList(
        iconsList: List<Int>,
        iconCategory: IconCategory
    ) {
        for (i in iconsList.indices) {
            addIconResource(iconCategory.id, iconsList[i])
        }
    }

    private suspend fun addIconResource(iconCategory: Int?, iconResource: Int) {
        launchIo {
            IconResourcesUseCase.addNewIconResource(
                dbIconResources,
                IconsResource(
                    iconCategory = iconCategory ?: 0,
                    iconResources = iconResource
                )
            )
            Message.log("---Add new icon resource---")
        }
    }

    private fun getDrawable(drawable: Int): Int {
        return app.resources.getIdentifier(
            app.resources.getResourceName(drawable),
            "drawable",
            packageName
        )
    }

}