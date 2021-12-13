package com.chico.myhomebookkeeping.ui.reports.fragments.categories

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
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import com.chico.myhomebookkeeping.utils.launchUi

class ReportsSelectCategoriesViewModel(
    val app: Application
//    private val categoriesList: List<Categories>,
//    private var selectedCategoriesSet: MutableSet<Int> = mutableSetOf()
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

    private var selectedCategoriesSetFromSp = setOf<Int>()

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
        launchUi {
            _categoriesItemsList.postValue(
                ConvToList.categoriesListToCategoriesItemsList(
                    CategoriesUseCase.getAllCategoriesSortIdAsc(db)
                )
            )
        }
    }

    fun saveSelectedCategories() {
        setSP.saveToSP(argsSelectedCategoriesSetKey, getSetSelectedCategories())
    }


    private fun getSetSelectedCategories(): Set<String> {
        val set = mutableSetOf<String>()
        if (_categoriesItemsList.value?.isNotEmpty() == true) {
            for (i in _categoriesItemsList.value?.indices!!) {
                if (_categoriesItemsList.value!![i].isChecked) {
                    set.add(i.toString())
                }
            }
        }
        return set
    }

    fun setCategoryChecked(id: Int) {
        _categoriesItemsList.value?.get(id)?.isChecked = true
    }

    fun setCategoryUnChecked(id: Int) {
        _categoriesItemsList.value?.get(id)?.isChecked = false
    }

    fun clearSelectedCategories() {
        _categoriesItemsList.value?.forEach {
            it.isChecked = false
        }
    }

    fun printResult() {
        _categoriesItemsList.value?.forEach {
            Message.log("category id = ${it.id}, name = ${it.name}, isChecked = ${it.isChecked}")
        }
    }

    fun getSelectedCategoriesFromSp(): Set<Int> {
        return selectedCategoriesSetFromSp
    }

//    fun getCategoriesList(): MutableLiveData<List<ReportsCategoriesItem>> {
//        return _categoriesItemsList
//    }

//
//    fun addCategoryInCategoriesSet(id: Int) {
//        selectedCategoriesSet.add(id)
//        getSetToString()
//    }
//
//    fun deleteCategoryInCategoriesSet(id: Int) {
//        selectedCategoriesSet.remove(id)
//        getSetToString()
//    }
//
//    private fun getSetToString() {
//        Message.log("size of items set = ${selectedCategoriesSet.size} ")
//        if (selectedCategoriesSet.size > 0) {
//            Message.log(" selected items ${selectedCategoriesSet.joinToString()}")
//        }
//        if (selectedCategoriesSet.size == 0) {
//            Message.log("items set is empty")
//        }
//    }
//
//    fun getSelectedCategoriesSet(): Set<Int> {
//        return selectedCategoriesSet.sorted().toSet()
//    }
//
//    fun getCategoriesList(): List<Categories> {
//        return categoriesList
//    }
//
//    fun eraseSelectedCategories() {
//        selectedCategoriesSet.clear()
//        getSetToString()
//    }
//
//    fun addAllInSelectedCategories() {
//        for (i in categoriesList.indices) {
//            Message.log("add in categories set ${i + 1}")
//            addInCategoriesSet(i)
//        }
//        getSetToString()
//    }
//
//    fun addAllIncomeInSelectedCategories() {
//        eraseSelectedCategories()
//        for (i in categoriesList.indices) {
//            if (categoriesList[i].isIncome) {
//                addInCategoriesSet(i)
//            }
//        }
//        getSetToString()
//    }
//
//    fun addAllSpendingInSelectedCategories() {
//        eraseSelectedCategories()
//        for (i in categoriesList.indices){
//            if (!categoriesList[i].isIncome){
//                addInCategoriesSet(i)
//            }
//        }
//        getSetToString()
//    }
//
//    private fun addInCategoriesSet(i: Int) {
//        selectedCategoriesSet.add(categoriesList[i].categoriesId ?: 0)
//    }
}