package com.chico.myhomebookkeeping.ui.cashAccount

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.SaveARGS
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.runBlocking

class CashAccountViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: CashAccountDao = dataBase.getDataBase(app.applicationContext).cashAccountDao()

    private val spName by lazy { Constants.SP_NAME }
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()
    private val argsForQuery = Constants.FOR_QUERY_CASH_ACCOUNT_KEY
    private val argsForCreate = Constants.FOR_CREATE_CASH_ACCOUNT_KEY

    private val saveARGS = SaveARGS(spEditor)

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

    fun loadCashAccounts() {
        launchIo {
            _cashAccountsList.postValue(db.getAllCashAccounts())
        }
    }

    fun saveData(navControlHelper: NavControlHelper) {
        saveARGS.checkAndSaveToSP(
            navControlHelper,
            argsForQuery,
            argsForCreate,
            _selectedCashAccount.value?.cashAccountId
        )
    }

    fun loadSelectedCashAccount(selectedId: Int) {
        launchIo {
            _selectedCashAccount.postValue(CashAccountsUseCase.getOneCashAccount(db, selectedId))
        }
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

    fun addNewCashAccount(name: String, number: String) {
        val newCashAccount = CashAccount(name, number)
        CashAccountsUseCase.addNewCashAccount(
            db,
            newCashAccount
        )
        loadCashAccounts()
    }

    fun saveChangedCashAccount(name: String, number: String) = runBlocking {
        Log.i("TAG", "fun saveChangedCashAccount name = $name, number = $number")
        CashAccountsUseCase.changeCashAccountLine(
            db = db,
            id = _changeCashAccount.value?.cashAccountId ?: 0,
            name = name,
            number = number
        )
        loadCashAccounts()
    }


}