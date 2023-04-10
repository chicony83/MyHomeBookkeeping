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
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FirstLaunchSelectCurrenciesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val _firstLaunchCurrenciesList = MutableLiveData<List<Currencies>>()
    val firstLaunchCurrenciesList: LiveData<List<Currencies>>
        get() = _firstLaunchCurrenciesList

    private val _selectedCurrenciesList = MutableLiveData<List<Currencies>>()
    val selectedCurrenciesList: LiveData<List<Currencies>>
        get() = _selectedCurrenciesList

    private val _onDefaultCurrencyAdded = MutableSharedFlow<Unit>()
    val onDefaultCurrencyAdded: SharedFlow<Unit> = _onDefaultCurrencyAdded.asSharedFlow()

    private val emptyCurrency = Currencies("", "", "", null, null)

    init {
        viewModelScope.launch {
            _firstLaunchCurrenciesList.postValue(FirstLaunchCurrenciesList.getCurrenciesList())
            _selectedCurrenciesList.postValue(listOf<Currencies>())
        }
    }

    fun moveCurrencyToSelectList(iso4217: String) {
        if (_firstLaunchCurrenciesList.value?.isNotEmpty() == true) {

            var currencyForAdd: Currencies = emptyCurrency
            _firstLaunchCurrenciesList.apply {
                val firstLaunchCurrenciesListAsList =
                    _firstLaunchCurrenciesList.value?.toMutableList()
                val id = getSelectedId(iso4217, firstLaunchCurrenciesListAsList)
                currencyForAdd =
                    findSelectedCurrencyInMutableList(id, firstLaunchCurrenciesListAsList)
                        ?: currencyForAdd

                _firstLaunchCurrenciesList.postValue(
                    removeSelectedCurrencyFromMutableList(id, firstLaunchCurrenciesListAsList)
                )

            }
            postingSelectedCurrenciesList(currencyForAdd)
        }
    }

    private fun findSelectedCurrencyInMutableList(
        id: Int,
        list: MutableList<Currencies>?
    ): Currencies? {
        return list?.get(id)
    }

    private fun removeSelectedCurrencyFromMutableList(
        id: Int,
        list: MutableList<Currencies>?
    ): MutableList<Currencies>? {

        return list?.apply { removeAt(id) }

//        _firstLaunchCurrenciesList.value =
//            _firstLaunchCurrenciesList.value?.toMutableList()?.apply {
//                removeAt(id)
//            }
    }


    private fun postingSelectedCurrenciesList(currencyForAdd: Currencies) {
        val listForPost: MutableList<Currencies>? =
            _selectedCurrenciesList.value?.toMutableList()
        Message.log("-- size of list${listForPost?.size}")
        listForPost?.add(currencyForAdd)
        _selectedCurrenciesList.postValue(sortedByISO(listForPost!!))
        Message.log("size after add ${_selectedCurrenciesList.value?.size}")
    }

    private fun getSelectedId(
        iso4217: String,
        list: MutableList<Currencies>?
    ): Int {
        var id = 0
        for (i in 0 until list?.size!!) {
            if (list[i].iso4217?.equals(iso4217) == true) {
                id = i
            }
        }
        return id
    }

    fun moveCurrencyToFirstLaunchCurrenciesList(iso4217: String) {
//        Message.log("move back")
        if (_selectedCurrenciesList.value?.isNotEmpty() == true) {
            var currencyForRemove: Currencies = emptyCurrency
            _selectedCurrenciesList.apply {
                val selectCurrenciesListAsList = _selectedCurrenciesList.value?.toMutableList()
                val id = getSelectedId(iso4217, selectCurrenciesListAsList)
                currencyForRemove =
                    findSelectedCurrencyInMutableList(id, selectCurrenciesListAsList)
                        ?: currencyForRemove
                _selectedCurrenciesList.postValue(
                    removeSelectedCurrencyFromMutableList(id, selectCurrenciesListAsList)
                )
            }
            postingFirstLaunchCurrenciesList(currencyForRemove)
        }

    }

    private fun postingFirstLaunchCurrenciesList(currencyForAdd: Currencies) {
        val listForPost: MutableList<Currencies>? =
            _firstLaunchCurrenciesList.value?.toMutableList()
        listForPost?.add(currencyForAdd)
        _firstLaunchCurrenciesList.postValue(sortedByISO(listForPost!!))
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