package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.SaveARGS
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val argsForQuery = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsForCreate = Constants.FOR_CREATE_CATEGORY_KEY
    private val argsIncomeSpending = Constants.FOR_QUERY_CATEGORIES_INCOME_SPENDING_KEY
    private val argsIncome = Constants.FOR_QUERY_INCOME
    private val argsSpending = Constants.FOR_QUERY_SPENDING
    private val argsNone = Constants.FOR_QUERY_NONE

    private val saveARGS = SaveARGS(spEditor)

    init {
        loadCategories()
    }

    private val _categoriesList = MutableLiveData<List<Categories>>()
    val categoriesList: LiveData<List<Categories>>
        get() = _categoriesList

    private val _selectedCategory = MutableLiveData<Categories?>()
    val selectedCategory: MutableLiveData<Categories?>
        get() = _selectedCategory

    private var _changeCategory = MutableLiveData<Categories?>()
    val changeCategory: LiveData<Categories?>
        get() = _changeCategory

    private var selectedIsIncomeSpending: String = argsNone

    fun loadCategories() {
        launchIo {
            _categoriesList.postValue(db.getAllCategory())
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
        saveARGS.saveIsIncomeCategoryToSP(
            argsIncomeSpending, selectedIsIncomeSpending
        )
    }

    private fun saveCategory(navControlHelper: NavControlHelper) {
        saveARGS.checkAndSaveToSP(
            navControlHelper,
            argsForQuery,
            argsForCreate,
            _selectedCategory.value?.categoriesId
        )
    }

    fun selectSpendingCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsSpending)
        saveIsIncomeCategory()
        saveCategory(navControlHelper)
    }

    fun selectIncomeCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsIncome)
        saveIsIncomeCategory()
        saveCategory(navControlHelper)
    }

    fun selectAllCategories(navControlHelper: NavControlHelper) {
        isIncomeSpendingSetNone()
        resetCategoryForSelect()
        saveIsIncomeCategory()
        saveCategory(navControlHelper)
    }

    fun selectIdCategory(navControlHelper: NavControlHelper) {
        isIncomeSpendingSetNone()
        saveIsIncomeCategory()
        saveCategory(navControlHelper)
    }

    private fun isIncomeSpendingSetNone() {
        selectedIsIncomeSpending = argsNone
    }

    fun selectToChange() {
        _changeCategory.postValue(_selectedCategory.value)
        resetCategoryForSelect()
    }

    fun saveChangedCategory(name: String, isIncome: Boolean) {
        CategoriesUseCase.changeCategoryLine(
            db, _changeCategory.value?.categoriesId ?: 0,
            name, isIncome
        )
        loadCategories()
        resetCategoryForChange()
    }
}