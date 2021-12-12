package com.chico.myhomebookkeeping.ui.reports.fragments.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.ConvToList
import com.chico.myhomebookkeeping.utils.launchUi

class ReportsSelectCategoriesViewModel(
    val app: Application
//    private val categoriesList: List<Categories>,
//    private var selectedCategoriesSet: MutableSet<Int> = mutableSetOf()
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    private var _categoriesItemsList = MutableLiveData<List<ReportsCategoriesItem>>()
    val categoriesItemsList: LiveData<List<ReportsCategoriesItem>>
        get() = _categoriesItemsList

    init {
        loadCategories()
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