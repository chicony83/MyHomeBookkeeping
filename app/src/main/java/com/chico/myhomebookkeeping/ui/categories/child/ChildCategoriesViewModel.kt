package com.chico.myhomebookkeeping.ui.categories.child

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.ChildCategoriesDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.ParentCategory
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.ChildCategoriesUseCase
import com.chico.myhomebookkeeping.enums.SortingCategories
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.SetTextOnButtons
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

class ChildCategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: ChildCategoriesDao =
        dataBase.getDataBase(app.applicationContext).childCategoriesDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

//    private val argsForCreate = Constants.FOR_CREATE_CATEGORY_KEY
//    private val argsForQuery = Constants.FOR_QUERY_CATEGORY_KEY
//    private val argsForChange = Constants.FOR_CHANGE_CATEGORY_KEY

    private val argsIncomeSpending = Constants.ARGS_QUERY_PAYMENT_CATEGORIES_INCOME_SPENDING_KEY
    private val argsIncome = Constants.ARGS_QUERY_PAYMENT_INCOME
    private val argsSpending = Constants.ARGS_QUERY_PAYMENT_SPENDING
    private val argsNone = Constants.FOR_QUERY_NONE
    private val argsSortingCategories = Constants.SORTING_CATEGORIES

    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)

    private val _selectedChildCategory = MutableLiveData<ChildCategory?>()
    val selectedChildCategory: LiveData<ChildCategory?> get() = _selectedChildCategory

    private val _categoriesList = MutableLiveData<List<ChildCategory>>()
    val categoriesList: LiveData<List<ChildCategory>> get() = _categoriesList

    private val _parentCategoryNameRes = MutableLiveData<Int?>()
    val parentCategoryNameRes: LiveData<Int?> get() = _parentCategoryNameRes

    private val _selectedCategory = MutableLiveData<ChildCategory?>()
    val selectedCategory: MutableLiveData<ChildCategory?> get() = _selectedCategory

    private var _changeCategory = MutableLiveData<ChildCategory?>()
    val changeCategory: LiveData<ChildCategory?> get() = _changeCategory

    private var _sortedByTextOnButton = MutableLiveData<String>()
    val sortedByTextOnButton: LiveData<String> get() = _sortedByTextOnButton

    private var selectedIsIncomeSpending: String = argsNone
    private var sortingCategoriesStringSP = getSP.getString(argsSortingCategories)
    private val setTextOnButtons = SetTextOnButtons(app.resources)

//    init {
//        loadCategories()
//    }

    fun loadCategories(parentCategoryNameRes: Int?) {
        sortingCategoriesStringSP = getSortingValueFromSP()
//        Message.log("get sortingCategoriesStringSP = $sortingCategoriesStringSP")
        launchIo {
            when (sortingCategoriesStringSP) {
                SortingCategories.NumbersByASC.toString() -> {
                    _categoriesList.postValue(ChildCategoriesUseCase.getAllCategoriesSortIdAsc(db).filter {
                        it.parentNameRes==parentCategoryNameRes
                    })
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_ASC))
                }
                SortingCategories.NumbersByDESC.toString() -> {
                    _categoriesList.postValue(ChildCategoriesUseCase.getAllCategoriesSortIdDesc(db).filter {
                        it.parentNameRes==parentCategoryNameRes
                    })
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_DESC))
                }
                SortingCategories.AlphabetByASC.toString() -> {
                    _categoriesList.postValue(ChildCategoriesUseCase.getAllCategoriesSortNameAsc(db).filter {
                        it.parentNameRes==parentCategoryNameRes
                    })
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_ASC))
                }
                SortingCategories.AlphabetByDESC.toString() -> {
                    _categoriesList.postValue(ChildCategoriesUseCase.getAllCategoriesSortNameDesc(db).filter {
                        it.parentNameRes==parentCategoryNameRes
                    })
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
                }
                else -> {
                    _categoriesList.postValue(ChildCategoriesUseCase.getAllCategoriesSortNameAsc(db).filter {
                        it.parentNameRes==parentCategoryNameRes
                    })
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
                }
            }
        }
    }

    private fun getString(string: Int) = app.getString(string)

    private fun setTextOnButton(string: String) {
        launchUi {
            setTextOnButtons.setTextOnSortingCategoriesButton(_sortedByTextOnButton, string)
        }
    }
    fun setParentCategory(parentCategoryNameRes: Int?) {
        _parentCategoryNameRes.value = parentCategoryNameRes
    }

    private fun getSortingValueFromSP(): String? {
        return getSP.getString(argsSortingCategories)
    }

    fun reloadCategories() {
        loadCategories(parentCategoryNameRes.value)
    }

    private fun reloadCategories(long: Long) {
        if (long > 0) {
            loadCategories(parentCategoryNameRes.value)
//            Log.i("TAG", "recycler reloaded")
        }
    }

    suspend fun loadSelectedCategory(selectedId: Int): ChildCategory? {
        return ChildCategoriesUseCase.getOneCategory(db, selectedId)
    }

    private fun resetCategoryForSelect() {
        launchIo {
            _selectedCategory.postValue(null)
        }
    }

    fun setSelectedChildCategory(childCategory: ChildCategory?) {
        _selectedChildCategory.value = childCategory
    }

//    fun resetCategoryForChange() {
//        launchIo {
//            _changeCategory.postValue(null)
//        }
//    }

    private fun setIsIncomeCategoriesSelect(value: String) {
        selectedIsIncomeSpending = value
    }

    private fun saveIsIncomeCategory() {
        setSP.saveIsIncomeCategoryToSP(
            argsIncomeSpending, selectedIsIncomeSpending
        )
    }

//    private fun saveData(navControlHelper: NavControlHelper) {
//        setSP.checkAndSaveToSP(
//            navControlHelper = navControlHelper,
//            argsForNew = argsForCreate,
//            argsForChange = argsForChange,
//            argsForQuery = argsForQuery,
//            id = _selectedCategory.value?.categoriesId
//        )
//    }

    fun saveData(navControlHelper: NavControlHelper, id: Int) {
        setSP.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            id = id
        )
//
//        setSP.checkAndSaveToSP(
//            navControlHelper = navControlHelper,
//            argsForNew = argsForCreate,
//            argsForChange = argsForChange,
//            argsForQuery = argsForQuery,
//            id = id
//        )
    }

    fun selectSpendingCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsSpending)
        saveIsIncomeCategory()
        saveData(navControlHelper, -1)
    }

    fun selectIncomeCategory(navControlHelper: NavControlHelper) {
        resetCategoryForSelect()
        setIsIncomeCategoriesSelect(argsIncome)
        saveIsIncomeCategory()
        saveData(navControlHelper, -1)
    }

    fun selectAllCategories(navControlHelper: NavControlHelper) {
        isIncomeSpendingSetNone()
        resetCategoryForSelect()
        saveIsIncomeCategory()
        saveData(navControlHelper, -1)
    }

    private fun isIncomeSpendingSetNone() {
        selectedIsIncomeSpending = argsNone
    }

    fun addNewCategory(newCategory: ChildCategory): Long = runBlocking {
        val add = async {
            ChildCategoriesUseCase.addNewCategory(
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

    private fun getNamesOfItems(items: List<ChildCategory>): MutableList<String> {
        val names = mutableListOf<String>()
        for (element in items) element.nameRes?.let { app.getString(it) }?.let { names.add(it) }
        return names
    }

    private fun getItemsList(): List<ChildCategory>? {
        return categoriesList.value?.toList()
    }

    fun setSortingCategories(sorting: String) {
        setSP.saveToSP(argsSortingCategories, sorting)
    }

    fun saveChangedCategory(
        db: ChildCategoriesDao = dataBase.getDataBase(app).childCategoriesDao(),
        id: Long,
        nameRes: Int,
        parentNameRes: Int,
        iconResource: Int
    ) =
        runBlocking {
            val change = async {
                ChildCategoriesUseCase.changeCategoryLine(
                    db = db,
                    id = id,
                    nameRes = nameRes,
                    parentNameRes = parentNameRes,
                    iconResource = iconResource
                )
            }
            reloadCategories(change.await().toLong())
        }

    fun resetSelectedChildCategory() {
        _selectedChildCategory.value=null
    }
}