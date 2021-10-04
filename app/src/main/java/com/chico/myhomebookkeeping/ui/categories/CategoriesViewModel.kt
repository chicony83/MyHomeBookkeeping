package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.enums.SortingCategories
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val argsForCreate = Constants.FOR_CREATE_CATEGORY_KEY
    private val argsForQuery = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsForChange = Constants.FOR_CHANGE_CATEGORY_KEY

    private val argsIncomeSpending = Constants.FOR_QUERY_CATEGORIES_INCOME_SPENDING_KEY
    private val argsIncome = Constants.FOR_QUERY_INCOME
    private val argsSpending = Constants.FOR_QUERY_SPENDING
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsSortingCategories = Constants.SORTING_CATEGORIES

    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)

    private val _categoriesList = MutableLiveData<List<Categories>>()
    val categoriesList: LiveData<List<Categories>> get() = _categoriesList

    private val _selectedCategory = MutableLiveData<Categories?>()
    val selectedCategory: MutableLiveData<Categories?> get() = _selectedCategory

    private var _changeCategory = MutableLiveData<Categories?>()
    val changeCategory: LiveData<Categories?> get() = _changeCategory

    private var _sortedByTextOnButton = MutableLiveData<String>()
    val sortedByTextOnButton: LiveData<String> get() = _sortedByTextOnButton

    private var selectedIsIncomeSpending: String = argsNone
    private var sortingCategoriesStringSP = getSP.getString(argsSortingCategories)
    private val setTextOnButtons = SetTextOnButtons(app.resources)
    private var additionalTextForTheButton = " "
    init {
        loadCategories()
    }

    private fun loadCategories() {
        sortingCategoriesStringSP = getSortingValueFromSP()
        Message.log("get sortingCategoriesStringSP = $sortingCategoriesStringSP")
        launchIo {
            when (sortingCategoriesStringSP) {
                SortingCategories.NumbersByASC.toString() -> {
                    _categoriesList.postValue(db.getAllCategoriesIdASC())
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_ASC))
                }
                SortingCategories.NumbersByDESC.toString() -> {
                    _categoriesList.postValue(db.getAllCategoriesIdDESC())
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_DESC))
                }
                SortingCategories.AlphabetByASC.toString() -> {
                    _categoriesList.postValue(db.getAllCategoriesNameASC())
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_ASC))
                }
                SortingCategories.AlphabetByDESC.toString() -> {
                    _categoriesList.postValue(db.getAllCategoriesNameDESC())
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
                }
                else -> {
                    _categoriesList.postValue(db.getAllCategoriesNameASC())
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
                }
            }
        }
    }

    private fun getString(string: Int) = app.getString(string)

    private fun setTextOnButton(string: String) {
        launchUi {
            setTextOnButtons.setTextOnSortingCategoriesButton(_sortedByTextOnButton,string)
        }
    }

    private fun getSortingValueFromSP(): String? {
        return getSP.getString(argsSortingCategories)
    }

    fun reloadCategories() {
        loadCategories()
    }

    private fun reloadCategories(long: Long) {
        if (long > 0) {
            loadCategories()
            Log.i("TAG", "recycler reloaded")
        }
    }

    fun loadSelectedCategory(selectedId: Int) {
        launchIo {
            _selectedCategory.postValue(CategoriesUseCase.getOneCategory(db, selectedId))
        }
    }

    fun resetCategoryForSelect() {
        launchIo {
            _selectedCategory.postValue(null)
        }
    }

    fun resetCategoryForChange() {
        launchIo {
            _changeCategory.postValue(null)
        }
    }

    private fun setIsIncomeCategoriesSelect(value: String) {
        selectedIsIncomeSpending = value
    }

    private fun saveIsIncomeCategory() {
        setSP.saveIsIncomeCategoryToSP(
            argsIncomeSpending, selectedIsIncomeSpending
        )
    }

    private fun saveData(navControlHelper: NavControlHelper) {
        setSP.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            argsForNew = argsForCreate,
            argsForChange = argsForChange,
            argsForQuery = argsForQuery,
            id = _selectedCategory.value?.categoriesId
        )
    }

    fun saveData(navControlHelper: NavControlHelper, id: Int) {
        setSP.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            argsForNew = argsForCreate,
            argsForChange = argsForChange,
            argsForQuery = argsForQuery,
            id = id
        )
    }

    fun selectSpendingCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsSpending)
        saveIsIncomeCategory()
        saveData(navControlHelper)
    }

    fun selectIncomeCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsIncome)
        saveIsIncomeCategory()
        saveData(navControlHelper)
    }

    fun selectAllCategories(navControlHelper: NavControlHelper) {
        isIncomeSpendingSetNone()
        resetCategoryForSelect()
        saveIsIncomeCategory()
        saveData(navControlHelper)
    }

    fun selectIdCategory(navControlHelper: NavControlHelper) {
        isIncomeSpendingSetNone()
        saveIsIncomeCategory()
        saveData(navControlHelper)
    }

    private fun isIncomeSpendingSetNone() {
        selectedIsIncomeSpending = argsNone
    }

    fun selectToChange() {
        _changeCategory.postValue(_selectedCategory.value)
        resetCategoryForSelect()
    }

    fun saveChangedCategory(name: String, isIncome: Boolean) = runBlocking {
        val save: Deferred<Int> = async {
            CategoriesUseCase.changeCategoryLine(
                db, _changeCategory.value?.categoriesId ?: 0,
                name, isIncome
            )
        }
        reloadCategories(save.await().toLong())
    }

    fun addNewCategory(newCategory: Categories): Long = runBlocking {
        val add = async {
            CategoriesUseCase.addNewCategory(
                db = db,
                newCategory = newCategory
            )
        }
        reloadCategories(add.await())
        return@runBlocking add.await()
    }

    fun getNamesList(): Any {
        val items = getItemsList()
        return if (!items.isNullOrEmpty()) getNamesOfItems(items)
        else -1
    }

    private fun getNamesOfItems(items: List<Categories>): MutableList<String> {
        val names = mutableListOf<String>()
        for (element in items) names.add(element.categoryName)
        return names
    }

    private fun getItemsList(): List<Categories>? {
        return categoriesList.value?.toList()
    }

    fun setSortingCategories(sorting: String) {
        setSP.saveToSP(argsSortingCategories, sorting)
    }
}