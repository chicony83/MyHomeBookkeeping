package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categorise
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).incomeDao()

    init {
        loadCategories()
    }

    private val _categoriesList = MutableLiveData<List<Categorise>>()
    val categoriesList: LiveData<List<Categorise>>
        get() = _categoriesList

    fun loadCategories() {
        launchIo {
            _categoriesList.postValue(db.getAllIncomeMoneyCategory())
        }
    }
}