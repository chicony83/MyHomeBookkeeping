package com.chico.myhomebookkeeping.ui.cashaccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchIo

class CashAccountViewModel (
    val app:Application
        ):AndroidViewModel(app){
    private val db:CashAccountDao = dataBase.getDataBase(app.applicationContext).cashAccountDao()

    init {
        loadCashAccounts()
    }
    private val _cashAccountsList = MutableLiveData<List<CashAccount>>()
    val cashAccountList:LiveData<List<CashAccount>>
        get() = _cashAccountsList

    fun loadCashAccounts() {
        launchIo {
            _cashAccountsList.postValue(db.getAllCashAccounts())
        }
    }

}