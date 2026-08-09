package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import kotlinx.coroutines.runBlocking

class ReportsSelectCategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName by lazy { Constants.SP_NAME }
    private val argsSelectedCategoriesSetKey = Constants.FOR_REPORTS_SELECTED_CATEGORIES_LIST_KEY
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(spEditor)

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    private var _categoriesItemsList = MutableLiveData<List<ReportsCategoriesItem>>()
    val categoriesItemsList: LiveData<List<ReportsCategoriesItem>>
        get() = _categoriesItemsList

    private val _selectedCount = MutableLiveData<Int>()
    val selectedCount: LiveData<Int>
        get() = _selectedCount

    private var allCategoriesItemsList: List<ReportsCategoriesItem> = emptyList()
    private var selectedCategoriesSetFromSp = setOf<Int>()
    private var currentFilter = FILTER_ALL

    init {
        getSelectedCategoriesSetFromSp()
        loadCategories()
    }

    private fun getSelectedCategoriesSetFromSp() {
        val result: MutableSet<String>? =
            getSP.getSelectedCategoriesSet(argsSelectedCategoriesSetKey)
        if (result?.size!! > 0) {
            selectedCategoriesSetFromSp = result.map {
                it.toInt()
            }.toSet()
        }
    }

    private fun loadCategories() {
        runBlocking {
            allCategoriesItemsList = ConvToList.categoriesListToCategoriesItemsList(
                CategoriesUseCase.getAllCategoriesSortIdAsc(db),
                AppLanguage.getSelectedTag(app.applicationContext)
            ).map { item ->
                item.copy(
                    isChecked = selectedCategoriesSetFromSp.isEmpty() ||
                            selectedCategoriesSetFromSp.contains(item.id)
                )
            }
            postFilteredCategories()
            postSelectedCount()
        }
    }

    fun saveSelectedCategories() {
        setSP.saveToSP(argsSelectedCategoriesSetKey, getSetSelectedCategories())
    }


    private fun getSetSelectedCategories(): Set<String> {
        val set = mutableSetOf<String>()
        if (allCategoriesItemsList.isNotEmpty()) {
            for (i in allCategoriesItemsList.indices) {
                if (allCategoriesItemsList[i].isChecked) {
                    val id = allCategoriesItemsList[i].id
                    //                    val id = i+1k
                    set.add(id.toString())
                    Message.log("add to save set $id")
                }
            }
        }
        return set
    }

    fun setCategoryChecked(id: Int) {
        allCategoriesItemsList.forEach {
            if (it.id == id) {
                it.isChecked = true
            }
        }
        postSelectedCount()
    }

    fun setCategoryUnChecked(id: Int) {
        allCategoriesItemsList.forEach {
            if (it.id == id) {
                it.isChecked = false
            }
        }
        postSelectedCount()
    }

    fun clearSelectedCategories() {
        allCategoriesItemsList.forEach {
            it.isChecked = false
        }
        postFilteredCategories()
        postSelectedCount()
    }

    fun printResult() {
        allCategoriesItemsList.forEach {
            Message.log("category id = ${it.id}, name = ${it.name}, isChecked = ${it.isChecked}")
        }
    }

    fun getSelectedCategoriesFromSp(): Set<Int> {
        return selectedCategoriesSetFromSp
    }

    fun newSelectedCategoriesSetSp() {
        selectedCategoriesSetFromSp = setOf<Int>()
    }

    fun selectAllCategories() {
        allCategoriesItemsList.forEach {
            it.isChecked = true
        }
        postFilteredCategories()
        postSelectedCount()
    }

    fun selectAllIncomeCategories() {
        allCategoriesItemsList.forEach {
            if (!it.isIncome) it.isChecked = false
            if (it.isIncome) it.isChecked = true
        }
        postFilteredCategories()
        postSelectedCount()
    }

    fun selectAllSpendingCategories() {
        allCategoriesItemsList.forEach {
            if (!it.isIncome) it.isChecked = true
            if (it.isIncome) it.isChecked = false
        }
        postFilteredCategories()
        postSelectedCount()
    }

    fun selectNone() {
        allCategoriesItemsList.forEach {
            it.isChecked = false
        }
        postFilteredCategories()
        postSelectedCount()
    }

    fun showAllCategories() {
        currentFilter = FILTER_ALL
        postFilteredCategories()
    }

    fun showIncomeCategories() {
        currentFilter = FILTER_INCOME
        postFilteredCategories()
    }

    fun showSpendingCategories() {
        currentFilter = FILTER_SPENDING
        postFilteredCategories()
    }

    private fun postFilteredCategories() {
        val filteredList = when (currentFilter) {
            FILTER_INCOME -> allCategoriesItemsList.filter { it.isIncome }
            FILTER_SPENDING -> allCategoriesItemsList.filter { !it.isIncome }
            else -> allCategoriesItemsList
        }
        _categoriesItemsList.postValue(filteredList)
    }

    private fun postSelectedCount() {
        _selectedCount.postValue(allCategoriesItemsList.count { it.isChecked })
    }

    companion object {
        private const val FILTER_ALL = "all"
        private const val FILTER_INCOME = "income"
        private const val FILTER_SPENDING = "spending"
    }
}
