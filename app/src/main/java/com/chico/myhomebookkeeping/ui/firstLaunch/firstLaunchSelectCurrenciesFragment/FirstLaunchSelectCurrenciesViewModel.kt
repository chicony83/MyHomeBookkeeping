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

    init {
        launchIo {
            _firstLaunchCurrenciesList.postValue(FirstLaunchCurrenciesList.getCurrenciesList())
        }
    }

}