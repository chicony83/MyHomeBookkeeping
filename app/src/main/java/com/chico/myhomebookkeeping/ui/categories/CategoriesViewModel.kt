package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.entity.Income
import com.chico.myhomebookkeeping.db.dataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class   CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: IncomeDao = dataBase.getDataBase(app.applicationContext).incomeDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _incomeCategoryList.postValue(db.getAllIncomeMoneyCategory())
        }
    }

    private val _incomeCategoryList = MutableLiveData<List<Income>>()
    val incomeCategoryList:LiveData<List<Income>>
    get() = _incomeCategoryList

    fun loadCategories(){
        CoroutineScope(Dispatchers.IO).launch {
            _incomeCategoryList.postValue(db.getAllIncomeMoneyCategory())
        }
    }
}