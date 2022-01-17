package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.domain.IconCategoriesUseCase
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.enums.icons.CashAccountIconNames
import com.chico.myhomebookkeeping.enums.icons.CategoryIconNames
import com.chico.myhomebookkeeping.enums.icons.CategoriesOfIconsNames
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

    private val _cardCashAccountItem = MutableLiveData<ItemOfFirstLaunch>()
    val cardCashAccountItem: LiveData<ItemOfFirstLaunch> get() = _cardCashAccountItem

    private val _cashCashAccountItem = MutableLiveData<ItemOfFirstLaunch>()
    val cashCashAccountItem: LiveData<ItemOfFirstLaunch> get() = _cashCashAccountItem

    private val _salaryCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val salaryCategoryItem: LiveData<ItemOfFirstLaunch> get() = _salaryCategoryItem

    private val _productsCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val productsCategoryItem: LiveData<ItemOfFirstLaunch> get() = _productsCategoryItem

    private val _fuelForCarCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val fuelForCarCategoryItem: LiveData<ItemOfFirstLaunch> get() = _fuelForCarCategoryItem

    private val _cellularCommunicationCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val cellularCommunicationCategoryItem: LiveData<ItemOfFirstLaunch> get() = _cellularCommunicationCategoryItem

    private val _creditsCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val creditsCategoryItem: LiveData<ItemOfFirstLaunch> get() = _creditsCategoryItem

    private val _medicinesCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val medicinesCategoryItem: LiveData<ItemOfFirstLaunch> get() = _medicinesCategoryItem

    private val _publicTransportCategoryItem = MutableLiveData<ItemOfFirstLaunch>()
    val publicTransportCategoryItem: LiveData<ItemOfFirstLaunch> get() = _publicTransportCategoryItem

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()
    private val setSP = SetSP(spEditor)
    private val uiHelper = UiHelper()

    private val packageName = app.packageName
    private val categoryIconsList = getCategoriesIconsList()
    private val cashAccountIconsMap: Map<String, Int> = getCashAccountIconsList()


    private fun getCashAccountIconsList() = mapOf<String, Int>(
        CashAccountIconNames.Card.name to getDrawable(R.drawable.cash_account_card),
        CashAccountIconNames.Cash.name to getDrawable(R.drawable.cash_account_cash),
        CashAccountIconNames.CardOff.name to getDrawable(R.drawable.cash_account_credit_card_off)
    )

    private fun getCategoriesIconsList() = mapOf<String, Int>(
        CategoryIconNames.Apartment.name to getDrawable(R.drawable.category_apartment),
        CategoryIconNames.Airplane.name to getDrawable(R.drawable.category_airplane),
        CategoryIconNames.ArrowsHorizontal.name to getDrawable(R.drawable.category_arrows_horizontal),
        CategoryIconNames.ArrowDropDown.name to getDrawable(R.drawable.category_arrow_drop_down),
        CategoryIconNames.ArrowDropUp.name to getDrawable(R.drawable.category_arrow_drop_up),
        CategoryIconNames.Bank.name to getDrawable(R.drawable.category_bank),
        CategoryIconNames.Build.name to getDrawable(R.drawable.category_build),
        CategoryIconNames.Bus.name to getDrawable(R.drawable.category_bus),
        CategoryIconNames.Cake.name to getDrawable(R.drawable.category_cake),
        CategoryIconNames.Car.name to getDrawable(R.drawable.category_car),
        CategoryIconNames.Celebration.name to getDrawable(R.drawable.category_celebration),
        CategoryIconNames.ChildFriendly.name to getDrawable(R.drawable.category_child_friendly),
        CategoryIconNames.Coffee.name to getDrawable(R.drawable.category_coffee),
        CategoryIconNames.Computer.name to getDrawable(R.drawable.category_computer),
        CategoryIconNames.GasStation.name to getDrawable(R.drawable.category_gas_station),
        CategoryIconNames.House.name to getDrawable(R.drawable.category_house),
        CategoryIconNames.Medical.name to getDrawable(R.drawable.category_medical),
        CategoryIconNames.Park.name to getDrawable(R.drawable.category_park),
        CategoryIconNames.PedalBike.name to getDrawable(R.drawable.category_pedal_bike),
        CategoryIconNames.People.name to getDrawable(R.drawable.category_people),
        CategoryIconNames.Person.name to getDrawable(R.drawable.category_person),
        CategoryIconNames.Pets.name to getDrawable(R.drawable.category_pets),
        CategoryIconNames.Phone.name to getDrawable(R.drawable.category_phone),
        CategoryIconNames.PhoneAndroid.name to getDrawable(R.drawable.category_phone_android),
        CategoryIconNames.PhoneIphone.name to getDrawable(R.drawable.category_phone_iphone),
        CategoryIconNames.Restaurant.name to getDrawable(R.drawable.category_restaurant),
        CategoryIconNames.Salon.name to getDrawable(R.drawable.category_salon),
        CategoryIconNames.School.name to getDrawable(R.drawable.category_school),
        CategoryIconNames.ShoppingCart.name to getDrawable(R.drawable.category_shopping_cart),
        CategoryIconNames.ShoppingCartAdd.name to getDrawable(R.drawable.category_shopping_cart_add),
        CategoryIconNames.Store.name to getDrawable(R.drawable.category_store),
        CategoryIconNames.Subway.name to getDrawable(R.drawable.category_subway),
        CategoryIconNames.TwoWheeler.name to getDrawable(R.drawable.category_two_wheeler)
    )


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
            ))
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
        val namesIconCategory = listOf<String>(
            CategoriesOfIconsNames.CashAccounts.name,
            CategoriesOfIconsNames.Categories.name,
            CategoriesOfIconsNames.Currencies.name
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
                    CategoriesOfIconsNames.CashAccounts.name -> addCashAccountsIconsInDB(
                        iconCategories[i]
                    )
                    CategoriesOfIconsNames.Categories.name -> addCategoriesIconsInDB(iconCategories[i])
                }
            }
        }
    }

    private suspend fun addCategoriesIconsInDB(iconCategory: IconCategory) {
        Message.log("---Add categories icons---")
        addIconsRecourseList(
            iconsList = categoryIconsList.values.toList(),
            iconCategory = iconCategory
        )
    }

    private suspend fun addCashAccountsIconsInDB(iconCategory: IconCategory) {
        Message.log("---Add Cash accounts icons---")
        addIconsRecourseList(
            iconsList = cashAccountIconsMap.values.toList(),
            iconCategory = iconCategory
        )
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
        Message.log("---Add new icon resource---")
        launchIo {
            IconResourcesUseCase.addNewIconResource(
                dbIconResources,
                IconsResource(
                    iconCategory = iconCategory ?: 0,
                    iconResources = iconResource
                )
            )
        }
    }

    private fun getDrawable(drawable: Int): Int {
        return app.resources.getIdentifier(
            app.resources.getResourceName(drawable),
            "drawable",
            packageName
        )
    }

    fun getNoImageImage(): Int {
        return getDrawable(R.drawable.no_image)
    }

    fun updateValues() {
        updateValuesOfCashAccounts()
        updateValuesOfCategories()
//        _salaryCategoryItem.postValue(FirstLaunchItem("income money", categoryIconsList))
    }

    private fun updateValuesOfCategories() {
        _salaryCategoryItem.postValue(
            CategoryIconNames.ArrowDropUp.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _productsCategoryItem.postValue(
            CategoryIconNames.ShoppingCart.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _fuelForCarCategoryItem.postValue(
            CategoryIconNames.GasStation.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _cellularCommunicationCategoryItem.postValue(
            CategoryIconNames.PhoneAndroid.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _creditsCategoryItem.postValue(
            CategoryIconNames.Bank.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _medicinesCategoryItem.postValue(
            CategoryIconNames.Medical.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
        _publicTransportCategoryItem.postValue(
            CategoryIconNames.Bus.name.let {
                ItemOfFirstLaunch(it, categoryIconsList[it])
            }
        )
    }

    private fun updateValuesOfCashAccounts() {
        _cardCashAccountItem.postValue(
            CashAccountIconNames.Card.name.let {
                ItemOfFirstLaunch(it, cashAccountIconsMap[it])
            }
        )
        _cashCashAccountItem.postValue(
            CashAccountIconNames.Cash.name.let {
                ItemOfFirstLaunch(it, cashAccountIconsMap[it])
            }
        )
    }

    data class ItemOfFirstLaunch(val name: String, val imageResource: Int?)

}