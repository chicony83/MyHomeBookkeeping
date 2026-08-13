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

    private var allCategoriesItemsList: List<ReportsCategoriesItem> = emptyList()
    private var selectedCategoriesSetFromSp = setOf<Int>()
    private var hasSavedSelection = false

    init {
        getSelectedCategoriesSetFromSp()
        loadCategories()
    }

    private fun getSelectedCategoriesSetFromSp() {
        val result: MutableSet<String>? =
            getSP.getSelectedCategoriesSet(argsSelectedCategoriesSetKey)
        hasSavedSelection = getSP.contains(argsSelectedCategoriesSetKey)
        selectedCategoriesSetFromSp = result.orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun loadCategories() {
        runBlocking {
            allCategoriesItemsList = ConvToList.categoriesListToCategoriesItemsList(
                CategoriesUseCase.getAllCategoriesSortIdAsc(db),
                AppLanguage.getSelectedTag(app.applicationContext)
            ).map { item ->
                item.copy(
                    isChecked = !hasSavedSelection ||
                            selectedCategoriesSetFromSp.contains(item.id)
                )
            }
            postCategories()
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
    }

    fun setCategoryUnChecked(id: Int) {
        allCategoriesItemsList.forEach {
            if (it.id == id) {
                it.isChecked = false
            }
        }
    }

    fun clearSelectedCategories() {
        allCategoriesItemsList.forEach {
            it.isChecked = false
        }
        postCategories()
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
        hasSavedSelection = true
    }

    fun selectAllCategories() {
        allCategoriesItemsList.forEach {
            it.isChecked = true
        }
        postCategories()
    }

    fun selectNone() {
        allCategoriesItemsList.forEach {
            it.isChecked = false
        }
        postCategories()
    }

    fun showAllCategories() {
        postCategories()
    }

    fun showIncomeCategories() {
        postCategories()
    }

    fun showSpendingCategories() {
        postCategories()
    }

    private fun postCategories() {
        _categoriesItemsList.postValue(allCategoriesItemsList)
    }
}
