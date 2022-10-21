package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.firstLaunch.FirstLaunchCurrenciesList
import com.chico.myhomebookkeeping.utils.launchIo

class FirstLaunchSelectCurrenciesViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val _firstLaunchCurrenciesList = MutableLiveData<List<Currencies>>()
    val firstLaunchCurrenciesList: LiveData<List<Currencies>>
        get() = _firstLaunchCurrenciesList

    private val _selectedCurrenciesList = MutableLiveData<List<Currencies>>()
    val selectedCurrenciesList: LiveData<List<Currencies>>
        get() = _selectedCurrenciesList

    init {
        launchIo {
            _firstLaunchCurrenciesList.postValue(FirstLaunchCurrenciesList.getCurrenciesList())
            _selectedCurrenciesList.postValue(
                listOf<Currencies>(
                )
            )
        }
    }

    fun moveCurrencyToSelectList(iso4217: String) {
        if (_firstLaunchCurrenciesList.value?.isNotEmpty() == true) {
//            var id = 0
            var currencyForAdd: Currencies = Currencies("", "", "", null, null)

            val id = getSelectedId(iso4217)

            currencyForAdd = findSelectedCurrencyInFirstLaunchCurrenciesList(currencyForAdd, id)
            removeSelectedCurrencyFromFirstLaunchCurrenciesList(id)
            postingSelectedCurrenciesList(currencyForAdd)
        }
    }

    private fun findSelectedCurrencyInFirstLaunchCurrenciesList(
        currencyForAdd: Currencies,
        id: Int
    ): Currencies {
        return _firstLaunchCurrenciesList.value?.get(id) ?: currencyForAdd
    }

    private fun removeSelectedCurrencyFromFirstLaunchCurrenciesList(id: Int) {
        _firstLaunchCurrenciesList.value =
            _firstLaunchCurrenciesList.value?.toMutableList()?.apply {
                removeAt(id)
            }
    }


    private fun postingSelectedCurrenciesList(currencyForAdd: Currencies) {
        val listForPost: MutableList<Currencies>? =
            _selectedCurrenciesList.value?.toMutableList()
        Message.log("-- size of list${listForPost?.size}")
        listForPost?.add(currencyForAdd)
        _selectedCurrenciesList.postValue(listForPost!!)
        Message.log("size after add ${_selectedCurrenciesList.value?.size}")
    }

    private fun getSelectedId(iso4217: String): Int {
        var id = 0
        for (i in 0 until _firstLaunchCurrenciesList.value!!.size) {
            if (_firstLaunchCurrenciesList.value?.get(i)?.iso4217?.equals(iso4217) == true) {
                id = i
            }
        }
        return id
    }
}