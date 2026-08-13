package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.ParentCategoriesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.db.simpleQuery.ReportsCreateSimpleQuery
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import kotlinx.coroutines.runBlocking

class ReportsSelectCategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()
    private val parentCategoriesDb: ParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).parentCategoriesDao()

    private var _categoriesItemsList = MutableLiveData<List<ReportsCategoriesItem>>()
    val categoriesItemsList: LiveData<List<ReportsCategoriesItem>>
        get() = _categoriesItemsList

    private var parentCategoriesItemsList: List<ReportsCategoriesItem> = emptyList()
    private var selectedCategoriesSetFromSp = setOf<Int>()
    private var selectedCategoryIds = setOf<Int>()
    private var hasSavedSelection = false
    private val paymentTypeId: Int
        get() = ReportsCreateSimpleQuery.paymentTypeIdForReportType(getSP.getString(Constants.REPORT_TYPE))

    init {
        getSelectedCategoriesSetFromSp()
        loadCategories()
    }

    private fun getSelectedCategoriesSetFromSp() {
        val result: MutableSet<String>? =
            getSP.getSelectedCategoriesSet(selectedCategoriesSetKey())
        hasSavedSelection = getSP.contains(selectedCategoriesSetKey())
        selectedCategoriesSetFromSp = result.orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
        selectedCategoryIds = selectedCategoriesSetFromSp
    }

    private fun loadCategories() {
        runBlocking {
            val categories = CategoriesUseCase.getAllCategoriesSortIdAsc(db)
            parentCategoriesItemsList = ConvToList.parentCategoriesListToReportsItemsList(
                ParentCategoriesUseCase.getAllParentCategoriesSortNameAsc(parentCategoriesDb),
                categories,
                AppLanguage.getSelectedTag(app.applicationContext)
            )

            if (!hasSavedSelection) {
                selectedCategoryIds = ConvToList.categoriesListToSelectedCategoriesSet(categories)
            }

            updateParentSelectionStates()

            postCategories()
        }
    }

    fun saveSelectedCategories() {
        setSP.saveToSP(selectedCategoriesSetKey(), getSetSelectedCategories())
    }


    private fun getSetSelectedCategories(): Set<String> {
        val set = selectedCategoryIds.map { it.toString() }.toSet()
        set.forEach {
            Message.log("add to save set $it")
        }
        return set
    }

    fun setCategoryChecked(id: Int) {
        parentCategoriesItemsList.find { it.id == id }?.let {
            selectedCategoryIds = selectedCategoryIds + it.categoryIds
        }
        updateParentSelectionStates()
    }

    fun setCategoryUnChecked(id: Int) {
        parentCategoriesItemsList.find { it.id == id }?.let {
            selectedCategoryIds = selectedCategoryIds - it.categoryIds
        }
        updateParentSelectionStates()
    }

    fun clearSelectedCategories() {
        selectedCategoryIds = emptySet()
        updateParentSelectionStates()
        postCategories()
    }

    fun printResult() {
        parentCategoriesItemsList.forEach {
            Message.log("category id = ${it.id}, name = ${it.name}, isChecked = ${it.isChecked}")
        }
    }

    fun getSelectedCategoriesFromSp(): Set<Int> {
        return selectedCategoriesSetFromSp
    }

    fun newSelectedCategoriesSetSp() {
        selectedCategoriesSetFromSp = setOf<Int>()
        hasSavedSelection = true
    }

    fun selectAllCategories() {
        selectedCategoryIds = parentCategoriesItemsList.flatMap { it.categoryIds }.toSet()
        updateParentSelectionStates()
        postCategories()
    }

    fun selectNone() {
        selectedCategoryIds = emptySet()
        updateParentSelectionStates()
        postCategories()
    }

    fun showAllCategories() {
        selectAllCategories()
    }

    fun showIncomeCategories() {
        selectedCategoryIds = parentCategoriesItemsList.flatMap { it.incomeCategoryIds }.toSet()
        updateParentSelectionStates()
        postCategories()
    }

    fun showSpendingCategories() {
        selectedCategoryIds = parentCategoriesItemsList.flatMap { it.spendingCategoryIds }.toSet()
        updateParentSelectionStates()
        postCategories()
    }

    private fun updateParentSelectionStates() {
        parentCategoriesItemsList = parentCategoriesItemsList.map { item ->
            val selectedChildrenCount = item.categoryIds.count { selectedCategoryIds.contains(it) }
            item.copy(
                isChecked = item.categoryIds.isNotEmpty() && selectedChildrenCount == item.categoryIds.size,
                isPartiallyChecked = selectedChildrenCount > 0 && selectedChildrenCount < item.categoryIds.size
            )
        }
    }

    private fun postCategories() {
        _categoriesItemsList.postValue(parentCategoriesItemsList)
    }

    private fun selectedCategoriesSetKey(): String {
        return "${Constants.FOR_REPORTS_SELECTED_CATEGORIES_LIST_KEY}_${paymentTypeId}"
    }
}
