package com.chico.myhomebookkeeping.ui.fastMoneyMovings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.FastMoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.FastMoneyMovement
import com.chico.myhomebookkeeping.domain.FastMoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo

class FastMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: FastMoneyMovementDao = dataBase.getDataBase(app.applicationContext).blankMoneyMovementDao()

    private val _fastMoneyMovementList = MutableLiveData<List<FastMoneyMovement>>()
    val fastMoneyMovementList:LiveData<List<FastMoneyMovement>> get() = _fastMoneyMovementList

    init {
        getBlanksList()
    }

    private fun getBlanksList() {
        launchIo {
            _fastMoneyMovementList.postValue(FastMoneyMovingUseCase.getAllBlanks(db))
        }
    }
}