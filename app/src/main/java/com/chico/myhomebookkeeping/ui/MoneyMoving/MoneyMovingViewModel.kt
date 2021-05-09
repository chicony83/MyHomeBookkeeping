package com.chico.myhomebookkeeping.ui.MoneyMoving

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.runBlocking

class MoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val argsCashAccountKey = Constants.QUERY_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.QUERY_CURRENCY_KEY
    private val argsCategoryKey = Constants.QUERY_CATEGORY_KEY

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val sharedPreferences:SharedPreferences = app.getSharedPreferences(spName,MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()

    private val _moneyMovementList = MutableLiveData<List<FullMoneyMoving>>()
    val moneyMovementList: LiveData<List<FullMoneyMoving>>
        get() = _moneyMovementList

    init {
        loadMoneyMovement()
    }

    fun loadMoneyMovement() {
        runBlocking {
            _moneyMovementList.postValue(MoneyMovingUseCase.getFullMoneyMovement(db))
        }
    }

}