package com.chico.myhomebookkeeping.ui.currencies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.utils.launchIo

class CurrenciesViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val db: CurrenciesDao = dataBase.getDataBase(app.applicationContext).currenciesDao()

    init {
        loadCurrencies()
    }

    private val _currenciesList = MutableLiveData<List<Currencies>>()
    val currenciesList: LiveData<List<Currencies>>
        get() = _currenciesList


    fun loadCurrencies() {
        launchIo {
            _currenciesList.postValue(db.getAllCurrency())
        }
    }
}