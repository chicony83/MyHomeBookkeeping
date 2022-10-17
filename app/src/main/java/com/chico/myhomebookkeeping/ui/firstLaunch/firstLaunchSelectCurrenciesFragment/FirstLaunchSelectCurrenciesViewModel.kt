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

//        _selectedCurrenciesList.let {
//            it.value?.forEach {
//                it1->
//            }
//        }
//        val selectedCurrency = _firstLaunchCurrenciesList.let {
//            it.value?.forEach { it1 ->
//                Message.log("${it1.iso4217?.contains(iso4217)}")
//            }
//        }


        var selectedCurrency: Currencies?

        for (i in 0 until _firstLaunchCurrenciesList.value!!.size){

            if (_firstLaunchCurrenciesList.value?.get(i)?.iso4217?.equals(iso4217) == true){
                selectedCurrency = _firstLaunchCurrenciesList.value!![i]
                Message.log(selectedCurrency.toString())
            }

        }

    }

}