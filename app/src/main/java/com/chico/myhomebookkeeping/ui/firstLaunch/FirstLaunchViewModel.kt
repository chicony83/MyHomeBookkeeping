package com.chico.myhomebookkeeping.ui.firstLaunch

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.domain.*
import com.chico.myhomebookkeeping.enums.icons.CashAccountIconNames
import com.chico.myhomebookkeeping.enums.icons.CategoriesOfIconsNames
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.icons.AddIcons
//import com.chico.myhomebookkeeping.icons.IconsMaps
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
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

    private val _cardCashAccountItem = MutableLiveData<Int>()
    val cardCashAccountItem: LiveData<Int> get() = _cardCashAccountItem

    private val _cashCashAccountItem = MutableLiveData<Int>()
    val cashCashAccountItem: LiveData<Int> get() = _cashCashAccountItem

    private val _salaryCategoryItem = MutableLiveData<Int>()
    val salaryCategoryItem: LiveData<Int> get() = _salaryCategoryItem

    private val _productsCategoryItem = MutableLiveData<Int>()
    val productsCategoryItem: LiveData<Int> get() = _productsCategoryItem

    private val _fuelForCarCategoryItem = MutableLiveData<Int>()
    val fuelForCarCategoryItem: LiveData<Int> get() = _fuelForCarCategoryItem

    private val _cellularCommunicationCategoryItem = MutableLiveData<Int>()
    val cellularCommunicationCategoryItem: LiveData<Int> get() = _cellularCommunicationCategoryItem

    private val _creditsCategoryItem = MutableLiveData<Int>()
    val creditsCategoryItem: LiveData<Int> get() = _creditsCategoryItem

    private val _medicinesCategoryItem = MutableLiveData<Int>()
    val medicinesCategoryItem: LiveData<Int> get() = _medicinesCategoryItem

    private val _publicTransportCategoryItem = MutableLiveData<Int>()
    val publicTransportCategoryItem: LiveData<Int> get() = _publicTransportCategoryItem

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)
    private val uiHelper = UiHelper()

    private val addIconCategories = AddIconCategories()

    @SuppressLint("NewApi")
    private val addIcons = AddIcons(dbIconResources, app.resources, app.opPackageName)

//    private val packageName = app.packageName
//    private val categoryIconsList = getCategoriesIconsList()
//    private val cashAccountIconsMap: Map<String, Int> = getCashAccountIconsList()

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }

    fun addFirstLaunchElements(
        listImageAndCheckBoxes: List<SelectedItemOfImageAndCheckBox>,
        listCurrencies: List<CheckBox>,
        listIncomeCategories: List<SelectedItemOfImageAndCheckBox>,
        listSpendingCategories: List<SelectedItemOfImageAndCheckBox>
    ) = runBlocking {
        val resultAddedIncomeCategories =
            async(Dispatchers.IO) { addIncomeCategories(listIncomeCategories) }
        val resultAddSpendingCategories =
            async(Dispatchers.IO) { addSpendingCategories(listSpendingCategories) }

        val resultAddCashAccount = async(Dispatchers.IO) { addCashAccounts(listImageAndCheckBoxes) }
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
//        Message.log("create payment")
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

    private fun addSpendingCategories(listSpendingCategories: List<SelectedItemOfImageAndCheckBox>): Long {
        var result: Long = 0
        launchIo {
            for (i in listSpendingCategories.indices) {
                result += addCategory(listSpendingCategories[i], false)
            }
        }
        return result
    }

    private fun addIncomeCategories(listIncomeCategories: List<SelectedItemOfImageAndCheckBox>): Long {
        var result: Long = 0
        launchIo {
            for (i in listIncomeCategories.indices) {
                result += addCategory(listIncomeCategories[i], true)
            }
        }
        return result
    }

    private suspend fun addCategory(item: SelectedItemOfImageAndCheckBox, isIncome: Boolean): Long {
        return dbCategories.addCategory(
            Categories(
                item.checkBox.text.toString(), isIncome, item.img
            )
        )
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

    private fun addCashAccounts(listImageAndCheckBoxes: List<SelectedItemOfImageAndCheckBox>): Boolean {
        for (i in listImageAndCheckBoxes.indices) {
            if (uiHelper.isCheckedCheckBox(listImageAndCheckBoxes[i].checkBox)) {
                addCashAccount(listImageAndCheckBoxes[i])
            }
        }
        return true
    }

    private fun addCashAccount(item: SelectedItemOfImageAndCheckBox) {
        val cashAccount = CashAccount(
            accountName = item.checkBox.text.toString(),
            bankAccountNumber = "",
            icon = item.img
        )
        launchIo {
            dbCashAccount.addCashAccount(cashAccount)
        }
    }

    fun addIconCategories() {
        launchIo {
            addIconCategories.add(dbIconCategories)
        }
    }

    suspend fun addIconsResources() {
        launchIo {
            var iconCategories = listOf<IconCategory>()
            while (iconCategories.size < 3) {
                delay(100)
//                Message.log("--- get icon categories")
                iconCategories = IconCategoriesUseCase.getAllIconCategories(dbIconCategories)
//                Message.log("--- size of Icon Categories ${iconCategories.size} ---")
            }

            for (i in iconCategories.indices) {
                when (iconCategories[i].iconCategoryName) {
                    CategoriesOfIconsNames.CashAccounts.name -> addCashAccountsIconsInDB(
                        iconCategories[i]
                    )
                    CategoriesOfIconsNames.Categories.name -> addCategoriesIconsInDB(
                        iconCategories[i]
                    )
                }
            }
        }
    }

    private fun addCategoriesIconsInDB(iconCategory: IconCategory) {
        addIcons.addCategoriesIconsInDB(iconCategory)
    }

    private fun addCashAccountsIconsInDB(iconCategory: IconCategory) {
        addIcons.addCashAccountsIconsInDB(iconCategory)
    }


    fun updateValues() {
        Message.log("update value")
        launchIo {
//            var listIconResources = listOf<IconsResource>()
            var listIconResources = getListOfIconResources()

            Message.log("listOfIconResources size = ${listIconResources.size}")

            if (listIconResources.isEmpty()) {
                listIconResources = getListOfIconResources()
            }
            updateValuesOfCashAccounts(listIconResources)
            updateValuesOfCategories(listIconResources)
        }
    }

    private suspend fun getListOfIconResources(): List<IconsResource> {
//        var listIconResources1 = listIconResources
        delay(1000)
        return IconResourcesUseCase.getIconsList(dbIconResources)
//        return listIconResources1
    }

    private fun updateValuesOfCategories(listIconResource: List<IconsResource>) {
//        val categoryIconsMap = IconsMaps().getCategoriesIconsMap()
//
//        _salaryCategoryItem.postValue(
//            CategoryIconNames.Wallet.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _productsCategoryItem.postValue(
//            CategoryIconNames.ShoppingCart.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _fuelForCarCategoryItem.postValue(
//            CategoryIconNames.GasStation.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _cellularCommunicationCategoryItem.postValue(
//            CategoryIconNames.PhoneAndroid.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _creditsCategoryItem.postValue(
//            CategoryIconNames.Bank.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _medicinesCategoryItem.postValue(
//            CategoryIconNames.Medical.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
//        _publicTransportCategoryItem.postValue(
//            CategoryIconNames.Bus.name.let {
//                ItemOfFirstLaunch(it, categoryIconsMap[it])
//            }
//        )
    }

    private fun updateValuesOfCashAccounts(listIconResource: List<IconsResource>) {

        launchUi {
            _cardCashAccountItem.postValue(
                getIconResource(listIconResource, CashAccountIconNames.Card.name)
            )
            _cashCashAccountItem.postValue(
                getIconResource(listIconResource, CashAccountIconNames.Cash.name)
                //                postItemOfFirstLaunch(listIconResource, CashAccountIconNames.Cash.name)
            )
        }


        //            CashAccountIconNames.Card.name.let {
//                ItemOfFirstLaunch(it, cashAccountIconsMap[it])
//            }

//        _cashCashAccountItem.postValue(
//            CashAccountIconNames.Cash.name.let {
//                ItemOfFirstLaunch(it, cashAccountIconsMap[it])
//            }
//        )
    }

    private fun getIconResource(listIconResource: List<IconsResource>, name: String) =
        listIconResource.find {
            it.iconName == name
        }?.iconResources
}
//    private fun postItemOfFirstLaunch(listIconResource: List<IconsResource>, name: String) =
//
//
////        ItemOfFirstLaunch(
////            name = name,
////            imageResource = listIconResource.find {
////                it.iconName == name
////            }?.iconResources
////        )
//
////    data class ItemOfFirstLaunch(val name: String, val imageResource: Int?)
//
//}