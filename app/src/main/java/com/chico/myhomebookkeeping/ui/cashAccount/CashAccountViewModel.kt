package com.chico.myhomebookkeeping.ui.cashAccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchIo
import kotlin.properties.Delegates

class CashAccountViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CashAccountDao = dataBase.getDataBase(app.applicationContext).cashAccountDao()

    private val _cashAccountsList = MutableLiveData<List<CashAccount>>()

    val cashAccountList: LiveData<List<CashAccount>>
        get() = _cashAccountsList

    init {
        loadCashAccounts()
    }

    fun loadCashAccounts() {
        launchIo {
            _cashAccountsList.postValue(db.getAllCashAccounts())
        }
    }


}