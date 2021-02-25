package com.chico.myhomebookkeeping.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chico.myhomebookkeeping.db.entity.Income

class CategoriesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is categories and places Fragment"
    }
    val text: LiveData<String> = _text

    private val _incomeCategoryList = MutableLiveData<List<Income>>()
    val incomeCategoryList:LiveData<List<Income>>
    get() = _incomeCategoryList
}