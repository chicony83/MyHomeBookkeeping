package com.chico.myhomebookkeeping.ui.categories

import android.app.Application
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.entity.Income
import com.chico.myhomebookkeeping.db.incomeCategoryDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class   CategoriesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: IncomeDao = incomeCategoryDB.getCategoryDB(app.applicationContext).incomeDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _incomeCategoryList.postValue(db.getAllIncomeMoneyCategory())
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is categories and places Fragment"
    }
    val text: LiveData<String> = _text

    private val _incomeCategoryList = MutableLiveData<List<Income>>()
    val incomeCategoryList:LiveData<List<Income>>
    get() = _incomeCategoryList

    fun loadCategories(){
        CoroutineScope(Dispatchers.IO).launch {
            _incomeCategoryList.postValue(db.getAllIncomeMoneyCategory())
        }
    }
}