package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.NewMoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()

    suspend fun loadCashAccount(id: Int) {
        launchIo {
            _selectedCashAccount.postValue(NewMoneyMovingUseCase.getOneString(dbCashAccount, id))
//            val result: List<CashAccount>? = NewMoneyMovingUseCase.getOneString(dbCashAccount,id)
//                    _selectedCashAccount.postValue(listOf(dbCashAccount.getOneCashAccount(id).first()))

        }
    }

    private val _selectedCurrency = MutableLiveData<List<Currencies>>()
    val selectedCurrency: LiveData<List<Currencies>>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<List<CashAccount>>()
    val selectedCashAccount: LiveData<List<CashAccount>>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<List<Categories>>()
    val selectedCategory: LiveData<List<Categories>>
        get() = _selectedCategory
}