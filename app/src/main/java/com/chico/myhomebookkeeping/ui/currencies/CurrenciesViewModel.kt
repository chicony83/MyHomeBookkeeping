package com.chico.myhomebookkeeping.ui.currencies

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class CurrenciesViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val db: CurrenciesDao = dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

//    private val argsForCreate = Constants.FOR_CREATE_CURRENCY_KEY
//    private val argsForQuery = Constants.FOR_QUERY_CURRENCY_KEY
//    private val argsForChange = Constants.FOR_CHANGE_CURRENCY_KEY

    private val setSP = SetSP(spEditor)

    private val _currenciesList = MutableLiveData<List<Currencies>>()
    val currenciesList: LiveData<List<Currencies>>
        get() = _currenciesList

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        launchIo {
            _currenciesList.postValue(db.getAllCurrenciesSortNameAsc())
        }
    }

    fun saveData(navControlHelper: NavControlHelper, id: Int) {

        setSP.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            id = id
        )

//        setSP.checkAndSaveToSP(
//            navControlHelper = navControlHelper,
//            argsForNew = argsForCreate,
//            argsForChange = argsForChange,
//            argsForQuery = argsForQuery,
//            id = id
//        )
    }

    suspend fun loadSelectedCurrency(selectedId: Int): Currencies? {
        return CurrenciesUseCase.getOneCurrency(db, selectedId)
    }

    fun addNewCurrency(newCurrency: Currencies): Long = runBlocking {
        val add = async {
            CurrenciesUseCase.addNewCurrency(
                db = db,
                newCurrency = newCurrency
            )
        }
        reloadCurrencies(add.await())
        return@runBlocking add.await()
    }

    private fun reloadCurrencies(long: Long) {
        if (long > 0) {
            loadCurrencies()
            Log.i("TAG", "recycler reloaded")
        }
    }

    fun getNamesList(): Any {
        val items = getItemsList()
        return if (!items.isNullOrEmpty()) return getNamesOfItems(items)
        else -1
    }

    private fun getNamesOfItems(items: List<Currencies>): MutableList<String> {
        val names = mutableListOf<String>()
        for (element in items) names.add(element.currencyName)
        return names
    }

    private fun getItemsList(): List<Currencies>? {
        return currenciesList.value?.toList()
    }

    fun saveChangedCurrency(id: Int, name: String, nameShort: String?, iSO: String?) = runBlocking {
        val save = async {
            CurrenciesUseCase.changeCurrencyLineNameShortNameIso(
                db = db,
                id = id,
                name = name,
                nameShort = nameShort,
                iSO = iSO
            )
        }
        reloadCurrencies(save.await().toLong())
    }
}

