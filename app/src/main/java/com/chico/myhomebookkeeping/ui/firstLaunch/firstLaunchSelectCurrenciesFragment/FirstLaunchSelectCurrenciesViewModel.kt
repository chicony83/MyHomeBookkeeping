package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FirstLaunchSelectCurrenciesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val _selectedCurrenciesList = MutableLiveData<List<Currencies>>()
    val selectedCurrenciesList: LiveData<List<Currencies>>
        get() = _selectedCurrenciesList

    private val _onDefaultCurrencyAdded = MutableSharedFlow<Unit>()
    val onDefaultCurrencyAdded: SharedFlow<Unit> = _onDefaultCurrencyAdded.asSharedFlow()

    init {
        viewModelScope.launch {
            _selectedCurrenciesList.postValue(listOf<Currencies>())
        }
    }

    fun toggleCurrencySelection(iso4217: String) {
        val selectedCurrencies = _selectedCurrenciesList.value.orEmpty().toMutableList()
        val selectedCurrency = selectedCurrencies.firstOrNull { it.iso4217 == iso4217 }

        if (selectedCurrency == null) {
            FirstLaunchCurrenciesList
                .getAllCurrenciesList()
                .firstOrNull { it.iso4217 == iso4217 }
                ?.let { selectedCurrencies.add(it) }
        } else {
            selectedCurrencies.removeAll { it.iso4217 == iso4217 }
        }

        _selectedCurrenciesList.postValue(sortedByISO(selectedCurrencies))
    }

    private fun sortedByISO(listForPost: MutableList<Currencies>): List<Currencies> {
        return listForPost.sortedBy { it.iso4217 }
    }

    fun isCurrenciesListNotEmpty(): Boolean {
        return !_selectedCurrenciesList.value.isNullOrEmpty()
    }

    fun addCurrenciesToDB(currencies: List<Currencies>) {
        viewModelScope.launch {
            CurrenciesUseCase.addCurrencies(dbCurrencies, currencies)
            _onDefaultCurrencyAdded.emit(Unit)
        }
    }

    fun getSelectedCurrencies(): List<Currencies> {
        return selectedCurrenciesList.value.orEmpty()
    }
}
