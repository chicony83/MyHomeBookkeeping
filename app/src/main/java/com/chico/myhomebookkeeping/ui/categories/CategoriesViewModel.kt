package com.chico.myhomebookkeeping.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoriesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is categories and places Fragment"
    }
    val text: LiveData<String> = _text
}