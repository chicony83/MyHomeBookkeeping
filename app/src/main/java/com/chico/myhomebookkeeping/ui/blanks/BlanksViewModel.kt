package com.chico.myhomebookkeeping.ui.blanks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.BlankMoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.BlankMoneyMovement
import com.chico.myhomebookkeeping.domain.BlankMoneyMovingUseCase
import com.chico.myhomebookkeeping.utils.launchIo

class BlanksViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: BlankMoneyMovementDao = dataBase.getDataBase(app.applicationContext).blankMoneyMovementDao()

    private val _blanksList = MutableLiveData<List<BlankMoneyMovement>>()
    val blanksList:LiveData<List<BlankMoneyMovement>> get() = _blanksList

    init {
        getBlanksList()
    }

    private fun getBlanksList() {
        launchIo {
            val result = BlankMoneyMovingUseCase.getAllBlanks(db)
        }
    }
}