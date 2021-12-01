package com.chico.myhomebookkeeping.ui.cashAccount

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class CashAccountViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CashAccountDao = dataBase.getDataBase(app.applicationContext).cashAccountDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val argsForCreate = Constants.FOR_CREATE_CASH_ACCOUNT_KEY
    private val argsForQuery = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsForChange = Constants.FOR_CHANGE_CASH_ACCOUNT_KEY

    private val saveARGS = SetSP(spEditor)

    private val _cashAccountsList = MutableLiveData<List<CashAccount>>()
    val cashAccountList: LiveData<List<CashAccount>>
        get() = _cashAccountsList

    private val _selectedCashAccount = MutableLiveData<CashAccount?>()
    val selectedCashAccount: MutableLiveData<CashAccount?>
        get() = _selectedCashAccount

    private val _changeCashAccount = MutableLiveData<CashAccount?>()
    val changeCashAccount: LiveData<CashAccount?>
        get() = _changeCashAccount

    init {
        loadCashAccounts()
    }

    private fun loadCashAccounts() {
        launchIo {
            _cashAccountsList.postValue(db.getAllCashAccountsSortNameAsc())
        }
    }

    fun saveData(navControlHelper: NavControlHelper) {
        saveARGS.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            argsForNew = argsForCreate,
            argsForChange = argsForChange,
            argsForQuery = argsForQuery,
            id = _selectedCashAccount.value?.cashAccountId
        )
    }
    fun saveData(navControlHelper: NavControlHelper, id: Int) {
        saveARGS.checkAndSaveToSP(
            navControlHelper = navControlHelper,
            argsForNew = argsForCreate,
            argsForChange = argsForChange,
            argsForQuery = argsForQuery,
            id = id
        )
    }

    suspend fun loadSelectedCashAccount(selectedId: Int):CashAccount? {
        return CashAccountsUseCase.getOneCashAccount(db,selectedId)
//        launchIo {
//            _selectedCashAccount.postValue(CashAccountsUseCase.getOneCashAccount(db, selectedId))
//        }
    }

    fun resetCashAccountForChange() {
        launchIo {
            _selectedCashAccount.postValue(null)
        }
    }

    fun resetCashAccountForSelect() {
        launchIo {
            _selectedCashAccount.postValue(null)
        }
    }

    fun selectedToChange() {
        _changeCashAccount.postValue(_selectedCashAccount.value)
        resetCashAccountForSelect()
    }

    fun addNewCashAccount(newCashAccount: CashAccount):Long = runBlocking {
        val add = async {
            CashAccountsUseCase.addNewCashAccount(
                db,
                newCashAccount
            )
        }
        reloadCategories(add.await())
        return@runBlocking add.await()
    }

    private fun reloadCategories(long: Long) {
        if (long > 0) {
            loadCashAccounts()
            Log.i("TAG", "recycler reloaded")
        }
    }

    fun saveChangedCashAccount(name: String, number: String) = runBlocking {
        val add = async {
            CashAccountsUseCase.changeCashAccountLine(
                db = db,
                id = _changeCashAccount.value?.cashAccountId ?: 0,
                name = name,
                number = number
            )
        }
        reloadCategories(add.await().toLong())
    }

    fun getNamesList(): Any {
        val items = getItemsList()
        return if (!items.isNullOrEmpty()) getNamesOfItems(items)
        else -1
    }

    private fun getNamesOfItems(items: List<CashAccount>): MutableList<String> {
        val names = mutableListOf<String>()
        for (element in items) names.add(element.accountName)
        return names
    }

    private fun getItemsList(): List<CashAccount>? {
        return cashAccountList.value?.toList()
    }

}
