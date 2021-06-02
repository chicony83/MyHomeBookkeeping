package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.helpers.ControlHelper
import com.chico.myhomebookkeeping.helpers.SaveARGS
import com.chico.myhomebookkeeping.utils.launchIo

class CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val argsForQuery = Constants.FOR_QUERY_CATEGORY_KEY
    private val argsForSelect = Constants.FOR_SELECT_CATEGORY_KEY

    private val saveARGS = SaveARGS(spEditor)

    init {
        loadCategories()
    }

    private val _categoriesList = MutableLiveData<List<Categories>>()
    val categoriesList: LiveData<List<Categories>>
        get() = _categoriesList

    private val _selectedCategory = MutableLiveData<Categories>()
    val selectedCategory: LiveData<Categories>
        get() = _selectedCategory

    fun loadCategories() {
        launchIo {
            _categoriesList.postValue(db.getAllCategory())
        }
    }
    fun saveData(controlHelper: ControlHelper) {
        saveARGS.checkAndSaveSP(controlHelper,argsForQuery,argsForSelect,_selectedCategory.value?.categoriesId)
    }

    fun loadSelectedCategory(selectedId:Int){
        launchIo {
            _selectedCategory.postValue(CategoriesUseCase.getOneCategory(db,selectedId))
        }
    }
}