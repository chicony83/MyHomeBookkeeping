package com.chico.myhomebookkeeping.ui.firstLaunch

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.domain.*
import com.chico.myhomebookkeeping.enums.icon.names.CashAccountIconNames
import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
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
    private val dbParentCategories: ParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).parentCategoriesDao()
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

//    private val addIconCategories = AddIconCategories()

    private var listIconResource = listOf<IconsResource>()
    private var selectedCashAccounts = listOf<FirstLaunchSetupItem>()
    private var selectedCategoryGroups = listOf<FirstLaunchCategoryGroupItem>()
    private var selectedDefaultCashAccountName = ""

    @SuppressLint("NewApi")
    private val addIcons = AddIcons(
        dbIconResources = dbIconResources,
        resources = app.resources,
        appPackageName = app.opPackageName
    )

//    private val packageName = app.packageName
//    private val categoryIconsList = getCategoriesIconsList()
//    private val cashAccountIconsMap: Map<String, Int> = getCashAccountIconsList()

    fun setIsFirstLaunchFalse() {
        setSP.setIsFirstLaunchFalse()
    }

    fun addFirstLaunchElements(
        listImageAndCheckBoxes: List<SelectedItemOfImageAndCheckBox>,
        categoryGroups: List<FirstLaunchCategoryGroupItem>,
        defaultCashAccountName: String
    ) = runBlocking {
        val resultAddedCategoryGroups =
            async(Dispatchers.IO) { addCategoryGroups(categoryGroups) }

        val resultAddCashAccount =
            async(Dispatchers.IO) { addCashAccounts(listImageAndCheckBoxes, defaultCashAccountName) }

//        Automatic fast payment creation is disabled for first launch.
//        Keep this block commented: it may be restored when quick payments get a new setup flow.
//        val sizeCategoriesList: Int = listIncomeCategories.size + listSpendingCategories.size
//
//        launchIo {
//            while (getCategoriesList().size < sizeCategoriesList) {
//                delay(100)
//                addFreeFastPayments()
//            }
//        }
    }

    fun saveFirstLaunchSelections(
        cashAccounts: List<FirstLaunchSetupItem>,
        categoryGroups: List<FirstLaunchCategoryGroupItem>
    ) {
        selectedCashAccounts = cashAccounts
        selectedCategoryGroups = categoryGroups
    }

    fun saveSelectedCashAccounts(cashAccounts: List<FirstLaunchSetupItem>) {
        selectedCashAccounts = cashAccounts
    }

    fun saveSelectedCategoryGroups(categoryGroups: List<FirstLaunchCategoryGroupItem>) {
        selectedCategoryGroups = categoryGroups
    }

    fun getSelectedCashAccounts(): List<FirstLaunchSetupItem> {
        return selectedCashAccounts
    }

    fun saveDefaultCashAccount(defaultCashAccountName: String) {
        selectedDefaultCashAccountName = defaultCashAccountName
    }

    fun saveStartFragment(startFragment: String) {
        setSP.saveToSP(Constants.START_FRAGMENT, startFragment)
    }

    fun getStartFragmentDestinationId(): Int {
        return when (sharedPreferences.getString(
            Constants.START_FRAGMENT,
            Constants.START_FRAGMENT_CATEGORIES
        )) {
            Constants.START_FRAGMENT_CATEGORIES -> com.chico.myhomebookkeeping.R.id.nav_categories
            Constants.START_FRAGMENT_JOURNAL -> com.chico.myhomebookkeeping.R.id.nav_money_moving
            else -> com.chico.myhomebookkeeping.R.id.nav_fast_payments_fragment
        }
    }

    fun addSavedFirstLaunchElements() = runBlocking {
        val resultAddedCategoryGroups =
            async(Dispatchers.IO) { addCategoryGroups(selectedCategoryGroups) }

        val resultAddCashAccount =
            async(Dispatchers.IO) {
                addSavedCashAccounts(selectedCashAccounts, selectedDefaultCashAccountName)
            }

//        Automatic fast payment creation is disabled for first launch.
//        Keep this block commented: it may be restored when quick payments get a new setup flow.
//        val sizeCategoriesList: Int =
//            selectedIncomeCategories.size + selectedSpendingCategories.size
//
//        launchIo {
//            while (getCategoriesList().size < sizeCategoriesList) {
//                delay(100)
//                addFreeFastPayments()
//            }
//        }
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

    private suspend fun addCategoryGroups(categoryGroups: List<FirstLaunchCategoryGroupItem>): Long {
        var result: Long = 0
        for (i in categoryGroups.indices) {
            val parentCategoryId = dbParentCategories.addNewParentCategory(
                ParentCategories(
                    name = categoryGroups[i].parentName,
                    icon = null,
                    parentCategoryOrder = i
                )
            )
            result += parentCategoryId
            for (j in categoryGroups[i].subcategories.indices) {
                result += addCategory(
                    name = categoryGroups[i].subcategories[j],
                    isIncome = categoryGroups[i].isIncome,
                    parentCategoryId = parentCategoryId.toInt(),
                    order = j
                )
            }
        }
        return result
    }

    private suspend fun addCategory(
        name: String,
        isIncome: Boolean,
        parentCategoryId: Int,
        order: Int
    ): Long {
        return dbCategories.addCategory(
            Categories(
                categoryName = name,
                isIncome = isIncome,
                icon = null,
                parentCategoryId = parentCategoryId,
                categoryOrder = order
            )
        )
    }

    private fun addCashAccounts(
        listImageAndCheckBoxes: List<SelectedItemOfImageAndCheckBox>,
        defaultCashAccountName: String
    ): Boolean {
        for (i in listImageAndCheckBoxes.indices) {
            if (uiHelper.isCheckedCheckBox(listImageAndCheckBoxes[i].checkBox)) {
                addCashAccount(listImageAndCheckBoxes[i], defaultCashAccountName)
            }
        }
        return true
    }

    private fun addSavedCashAccounts(
        cashAccounts: List<FirstLaunchSetupItem>,
        defaultCashAccountName: String
    ): Boolean {
        for (i in cashAccounts.indices) {
            addCashAccount(cashAccounts[i], defaultCashAccountName)
        }
        return true
    }

    private fun addCashAccount(
        item: SelectedItemOfImageAndCheckBox,
        defaultCashAccountName: String
    ) {
        val cashAccount = CashAccount(
            accountName = item.checkBox.text.toString(),
            bankAccountNumber = "",
            isCashAccountDefault = item.checkBox.text.toString() == defaultCashAccountName,
            icon = item.img
        )
        launchIo {
            dbCashAccount.addCashAccount(cashAccount)
        }
    }

    private fun addCashAccount(
        item: FirstLaunchSetupItem,
        defaultCashAccountName: String
    ) {
        val cashAccount = CashAccount(
            accountName = item.name,
            bankAccountNumber = "",
            isCashAccountDefault = item.name == defaultCashAccountName,
            icon = item.img
        )
        launchIo {
            dbCashAccount.addCashAccount(cashAccount)
        }
    }

    fun addIconCategories() {
        launchIo {
            AddIconCategories.add(dbIconCategories)
        }
    }

    suspend fun addIconsResources() {
        launchIo {
            var iconCategoriesList = listOf<IconCategory>()
            while (iconCategoriesList.size < 3) {
                delay(100)
//                Message.log("--- get icon categories")
                iconCategoriesList = IconCategoriesUseCase.getAllIconCategories(dbIconCategories)
//                Message.log("--- size of Icon Categories ${iconCategories.size} ---")
            }
            addIcons.addIconResources(iconCategoriesList)
        }
    }

//    private fun res(iconCategories: List<IconCategory>) {
//    }

//    private fun addCategoriesIconsInDB(iconCategory: IconCategory) {
//        addIcons.addCategoriesIconsInDB(iconCategory)
//    }
//
//    private fun addCashAccountsIconsInDB(iconCategory: IconCategory) {
//        addIcons.addCashAccountsIconsInDB(iconCategory)
//    }

    fun updateValues() {
        Message.log("update value")
        launchIo {
//            var listIconResources = listOf<IconsResource>()
            listIconResource = getListOfIconResources()

            Message.log("listOfIconResources size = ${listIconResource.size}")

            if (listIconResource.isEmpty()) {
                listIconResource = getListOfIconResources()
            }
            updateValuesOfCashAccounts()
            updateValuesOfCategories()
        }
    }

    private suspend fun getListOfIconResources(): List<IconsResource> {
        delay(1000)
        return IconResourcesUseCase.getIconsList(dbIconResources)
    }

    private fun updateValuesOfCategories() {

        _salaryCategoryItem.postValue(
            getIconResource(CategoryIconNames.Wallet.name)
        )
        _productsCategoryItem.postValue(
            getIconResource(CategoryIconNames.ShoppingCart.name)
        )

        _fuelForCarCategoryItem.postValue(
            getIconResource(CategoryIconNames.GasStation.name)
        )
        _cellularCommunicationCategoryItem.postValue(
            getIconResource(CategoryIconNames.PhoneAndroid.name)
        )
        _creditsCategoryItem.postValue(
            getIconResource(CategoryIconNames.Bank.name)
        )
        _medicinesCategoryItem.postValue(
            getIconResource(CategoryIconNames.Medical.name)
        )
        _publicTransportCategoryItem.postValue(
            getIconResource(CategoryIconNames.Bus.name)
        )
    }

    private fun updateValuesOfCashAccounts() {
        launchUi {
            _cardCashAccountItem.postValue(
                getIconResource(CashAccountIconNames.Card.name)
            )
            _cashCashAccountItem.postValue(
                getIconResource(CashAccountIconNames.Cash.name)
            )
        }
    }

    private fun getIconResource(name: String) =
        listIconResource.find {
            it.iconName == name
        }?.iconResources
}
