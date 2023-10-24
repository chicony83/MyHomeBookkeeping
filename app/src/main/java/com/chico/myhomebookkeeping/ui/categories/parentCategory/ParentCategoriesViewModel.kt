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
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ParentCategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val db: ParentCategoriesDao =
        dataBase.getDataBase(app.applicationContext).parentCategoriesDao()

    private val _parentCategoriesList = MutableLiveData<List<ParentCategories>>()
    val parentCategoriesList: LiveData<List<ParentCategories>> get() = _parentCategoriesList

    private val _selectedParentCategory = MutableLiveData<ParentCategories?>()
    val selectedParentCategory : MutableLiveData<ParentCategories?> get() = _selectedParentCategory



    init {
        loadParentCategories()
    }

    private fun loadParentCategories() {
        launchIo {
            _parentCategoriesList.postValue(
                ParentCategoriesUseCase.getAllParentCategoriesSortNameAsc(
                    db
                )
            )
        }
    }

    fun addNewParentCategory(parentCategory: ParentCategories): Long = runBlocking {
        val add = async {
            ParentCategoriesUseCase.addNewParentCategory(
                db = db,
                newParentCategory = parentCategory
            )
        }
        reloadParentCategories(add.await())
        return@runBlocking add.await()
    }

    private fun reloadParentCategories(long: Long) {
        if (long > 0) {
            loadParentCategories()
        }
    }

    fun getParentCategoriesList(): List<ParentCategories> {
        return _parentCategoriesList.value?.toList()!!
    }

    fun getSelectedParentCategory(id: Int)  {
        launchIo {
            _selectedParentCategory.postValue(ParentCategoriesUseCase.getSelectedParentCategory(db, id))
        }
    }

    fun eraseSelectedParentCategory(){
        _selectedParentCategory.postValue(null)
    }

}