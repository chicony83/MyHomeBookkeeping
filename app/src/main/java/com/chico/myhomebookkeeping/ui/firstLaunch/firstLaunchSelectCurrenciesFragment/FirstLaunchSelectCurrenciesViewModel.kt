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
        }
    }

    fun moveCurrencyToSelectList(iso4217: String) {

        var selectedCurrency: Currencies?



        if (_firstLaunchCurrenciesList.value?.isNotEmpty() == true) {
            var findingId = 0
            for (i in 0 until _firstLaunchCurrenciesList.value!!.size) {
                if (_firstLaunchCurrenciesList.value?.get(i)?.iso4217?.equals(iso4217) == true) {
                    val id: Currencies? = _firstLaunchCurrenciesList.value?.get(i)
//                    Message.log("$i")
//                    Message.log("$id")
                    findingId = i
                }
            }
            _firstLaunchCurrenciesList.value =
                _firstLaunchCurrenciesList.value?.toMutableList()?.apply {
                    removeAt(findingId)
                }
        }
    }
}