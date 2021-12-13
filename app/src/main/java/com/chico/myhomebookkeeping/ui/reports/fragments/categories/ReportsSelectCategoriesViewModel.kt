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
        runBlocking {
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
                    val id = i+1
                    set.add(id.toString())
                    Message.log("add to save set $id")
                }
            }
        }
        return set
    }

    fun setCategoryChecked(id: Int) {
//        Message.log("get id ${_categoriesItemsList.value?.get(id)?.id}")
//        for (i in _categoriesItemsList.value?.indices!!){
//            Message.log("num $i it id = ${_categoriesItemsList.value!![i].id}")
//        }
//        _categoriesItemsList.value?.contains( ReportsCategoriesItem(id))
//        for (i in _categoriesItemsList.value?.indices!!){
//            if (_categoriesItemsList.value?.get(i)?.id == id){
//                _categoriesItemsList.value?.get(i)?.isChecked = true
//            }
//        }
        _categoriesItemsList.value!!.forEach {
            if (it.id == id){
                it.isChecked = true
            }
        }
//        _categoriesItemsList.value?.get(id)?.isChecked = true
    }

    fun setCategoryUnChecked(id: Int) {
//        for (i in _categoriesItemsList.value?.indices!!){
//            if (_categoriesItemsList.value?.get(i)?.id == id){
//                _categoriesItemsList.value?.get(i)?.isChecked = false
//            }
//        }
        _categoriesItemsList.value!!.forEach {
            if (it.id == id){
                it.isChecked = false
            }
        }
//        _categoriesItemsList.value?.get(id)?.isChecked = false
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

    fun newSelectedCategoriesSetSp() {
        selectedCategoriesSetFromSp = setOf<Int>()
    }
}