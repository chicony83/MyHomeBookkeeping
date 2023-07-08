package com.chico.myhomebookkeeping.ui.categories

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
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.dao.UserParentCategoriesDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.ParentCategory
import com.chico.myhomebookkeeping.db.entity.UserParentCategory
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.ChildCategoriesUseCase
import com.chico.myhomebookkeeping.domain.ParentCategoriesUseCase
import com.chico.myhomebookkeeping.domain.entities.NormalizedCategory
import com.chico.myhomebookkeeping.domain.mappers.ParentCategoryMapper
import com.chico.myhomebookkeeping.domain.mappers.UserParentCategoryMapper
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

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: ParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).parentCategoriesDao()
    private val userParentCategoriesDao: UserParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).userParentCategoriesDao()

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

    private val _categoriesList = MutableLiveData<List<NormalizedCategory>>()
    val categoriesList: LiveData<List<NormalizedCategory>> get() = _categoriesList

    private val _selectedCategory = MutableLiveData<NormalizedCategory?>()
    val selectedCategory: MutableLiveData<NormalizedCategory?> get() = _selectedCategory

    private var _sortedByTextOnButton = MutableLiveData<String>()
    val sortedByTextOnButton: LiveData<String> get() = _sortedByTextOnButton

    private var selectedIsIncomeSpending: String = argsNone
    private var sortingCategoriesStringSP = getSP.getString(argsSortingCategories)
    private val setTextOnButtons = SetTextOnButtons(app.resources)

    init {
        loadCategories()
    }

    fun loadCategories() {
        sortingCategoriesStringSP = getSortingValueFromSP()
//        Message.log("get sortingCategoriesStringSP = $sortingCategoriesStringSP")
        launchIo {
            when (sortingCategoriesStringSP) {
                SortingCategories.NumbersByASC.toString() -> {
                    val composedCategories = mutableListOf<NormalizedCategory>().apply {
                        addAll(ParentCategoriesUseCase.getAllCategoriesSortIdAsc(db).map {
                            ParentCategoryMapper().map(it)
                        })
                        addAll(userParentCategoriesDao.getAllUserParentCategoriesSortIdASC().map {
                            UserParentCategoryMapper().map(it)
                        })
                    }

                    _categoriesList.postValue(composedCategories)
                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_ASC))
                }
//                SortingCategories.NumbersByDESC.toString() -> {
//                    _categoriesList.postValue(ParentCategoriesUseCase.getAllCategoriesSortIdDesc(db))
//                    setTextOnButton(getString(R.string.text_on_button_sorting_as_numbers_DESC))
//                }
//                SortingCategories.AlphabetByASC.toString() -> {
//                    _categoriesList.postValue(ParentCategoriesUseCase.getAllCategoriesSortNameAsc(db))
//                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_ASC))
//                }
//                SortingCategories.AlphabetByDESC.toString() -> {
//                    _categoriesList.postValue(ParentCategoriesUseCase.getAllCategoriesSortNameDesc(db))
//                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
//                }
//                else -> {
//                    _categoriesList.postValue(ParentCategoriesUseCase.getAllCategoriesSortNameAsc(db))
//                    setTextOnButton(getString(R.string.text_on_button_sorting_as_alphabet_DESC))
//                }
            }
        }
    }

    private fun getString(string: Int) = app.getString(string)

    private fun setTextOnButton(string: String) {
        launchUi {
            setTextOnButtons.setTextOnSortingCategoriesButton(_sortedByTextOnButton, string)
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
//            Log.i("TAG", "recycler reloaded")
        }
    }

    suspend fun loadSelectedCategory(selectedId: Long): ParentCategory? {
        return ParentCategoriesUseCase.getOneCategory(db, selectedId)
    }

    fun setSelectedCategory(normalizedCategory: NormalizedCategory?) {
            _selectedCategory.value = normalizedCategory
    }

    fun resetCategoryForSelect() {
            _selectedCategory.value = null
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

    fun addNewCategory(newCategory: ParentCategory): Long = runBlocking {
        val add = async {
            ParentCategoriesUseCase.addNewCategory(
                db = db,
                newCategory = newCategory
            )
        }
        reloadCategories(add.await())
        return@runBlocking add.await()
    }

//    fun getNamesList(): Any {
//        val items = getItemsList()
//        return if (!items.isNullOrEmpty()) getNamesOfItems(items)
//        else -1
//    }

    private fun getNamesOfItems(items: List<ParentCategory>): MutableList<String> {
        val names = mutableListOf<String>()
        for (element in items) names.add(app.applicationContext.getString(element.nameRes))
        return names
    }

//    private fun getItemsList(): List<ParentCategory>? {
//        return categoriesList.value?.toList()
//    }

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
}