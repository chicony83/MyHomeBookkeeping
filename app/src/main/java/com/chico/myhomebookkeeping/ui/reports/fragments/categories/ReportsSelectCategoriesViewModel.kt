package com.chico.myhomebookkeeping.ui.reports.fragments.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.helpers.Message

class ReportsSelectCategoriesViewModel(
    val app:Application
//    private val categoriesList: List<Categories>,
//    private var selectedCategoriesSet: MutableSet<Int> = mutableSetOf()
) :AndroidViewModel(app){
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