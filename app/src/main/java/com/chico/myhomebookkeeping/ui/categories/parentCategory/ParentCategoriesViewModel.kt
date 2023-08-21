package com.chico.myhomebookkeeping.ui.categories.parentCategory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.ParentCategoriesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.domain.ParentCategoriesUseCase
import com.chico.myhomebookkeeping.utils.launchIo

class ParentCategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val db:ParentCategoriesDao = dataBase.getDataBase(app.applicationContext).parentCategoriesDao()

    private val _parentCategoriesList = MutableLiveData<List<ParentCategories>>()
    val parentCategoriesList:LiveData<List<ParentCategories>> get() = _parentCategoriesList

    init {
        loadParentCategories()
    }

    private fun loadParentCategories() {
        launchIo {
            _parentCategoriesList.postValue(ParentCategoriesUseCase.getAllParentCategoriesSortNameAsc(db))
        }
    }

}