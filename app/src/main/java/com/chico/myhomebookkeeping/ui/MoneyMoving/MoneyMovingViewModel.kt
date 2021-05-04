package com.chico.myhomebookkeeping.ui.MoneyMoving

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val db: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()

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