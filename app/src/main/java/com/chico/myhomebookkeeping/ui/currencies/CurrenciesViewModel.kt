package com.chico.myhomebookkeeping.ui.currencies

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.SaveARGS
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.runBlocking

class CurrenciesViewModel(
    val app: Application,
) : AndroidViewModel(app) {
    private val db: CurrenciesDao = dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val argsForQuery = Constants.FOR_QUERY_CURRENCY_KEY
    private val argsForCreate = Constants.FOR_CREATE_CURRENCY_KEY

    private val saveARGS = SaveARGS(spEditor)

    private val _currenciesList = MutableLiveData<List<Currencies>>()
    val currenciesList: LiveData<List<Currencies>>
        get() = _currenciesList

    private val _selectedCurrency = MutableLiveData<Currencies?>()
    val selectedCurrency: MutableLiveData<Currencies?>
        get() = _selectedCurrency

    private var _changeCurrency = MutableLiveData<Currencies?>()
    val changeCurrency: LiveData<Currencies?>
        get() = _changeCurrency


    init {
        loadCurrencies()
    }

    fun loadCurrencies() {
        launchIo {
            _currenciesList.postValue(db.getAllCurrency())
        }
    }

    fun saveData(navControlHelper: NavControlHelper) {
        saveARGS.checkAndSaveToSP(
            navControlHelper,
            argsForQuery,
            argsForCreate,
            _selectedCurrency.value?.currencyId
        )
    }

    fun loadSelectedCurrency(selectedId: Int) {
        launchIo {
            _selectedCurrency.postValue(CurrenciesUseCase.getOneCurrency(db, selectedId))
        }
    }

    fun resetCurrencyForSelect() {
        launchIo {
            _selectedCurrency.postValue(null)
        }
    }

    fun resetCurrencyForChange() {
        launchIo {
            _changeCurrency.postValue(null)
        }
    }

    fun selectedToChange() {
        _changeCurrency.postValue(_selectedCurrency.value)
        resetCurrencyForSelect()
    }

    fun saveChangedCurrency(name: String) = runBlocking {
        CurrenciesUseCase.changeCurrencyLine(
            db = db,
            id = _changeCurrency.value?.currencyId ?: 0,
            name = name
        )
        loadCurrencies()
    }

    fun addNewCurrency(name: String) = runBlocking {
        val newCurrency = Currencies(name)
        CurrenciesUseCase.addNewCurrency(
            db,
            newCurrency
        )
        loadCurrencies()
    }
}