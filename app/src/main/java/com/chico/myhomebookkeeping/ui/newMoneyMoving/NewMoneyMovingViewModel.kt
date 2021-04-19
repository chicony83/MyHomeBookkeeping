package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val _selectedCurrency = MutableLiveData<List<Currencies>>()
    val selectedCurrency:LiveData<List<Currencies>>
    get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<List<CashAccount>>()
    val selectedCashAccount:LiveData<List<CashAccount>>
    get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<List<Categories>>()
    val selectedCategory:LiveData<List<Categories>>
    get() = _selectedCategory
}