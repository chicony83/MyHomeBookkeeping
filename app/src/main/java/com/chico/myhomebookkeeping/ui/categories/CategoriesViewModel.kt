package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    init {
        loadCategories()
    }

    private val _categoriesList = MutableLiveData<List<Categories>>()
    val categoriesList: LiveData<List<Categories>>
        get() = _categoriesList

    fun loadCategories() {
        launchIo {
            _categoriesList.postValue(db.getAllCategory())
        }
    }
}