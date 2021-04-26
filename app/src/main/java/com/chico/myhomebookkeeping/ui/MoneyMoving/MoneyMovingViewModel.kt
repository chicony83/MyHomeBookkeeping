package com.chico.myhomebookkeeping.ui.MoneyMoving

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

    private val _moneyMovementList = MutableLiveData<List<MoneyMovement>>()
    val moneyMovementList: LiveData<List<MoneyMovement>>
        get() = _moneyMovementList

    init {
        loadMoneyMovement()
    }

    fun loadMoneyMovement() {
        runBlocking {

            val list: List<MoneyMovement>? = MoneyMovingUseCase.getMoneyMovement(db)
            Log.i("TAG", "---List size = ${list?.size}")
            _moneyMovementList.postValue(db.getAllMovingMoney())
            Log.i("TAG", "---size---${_moneyMovementList.value?.first()?.amount}---")
        }
    }

}