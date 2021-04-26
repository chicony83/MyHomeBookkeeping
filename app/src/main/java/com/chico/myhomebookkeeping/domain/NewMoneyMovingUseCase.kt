package com.chico.myhomebookkeeping.domain

import android.content.Context
import android.util.Log
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.runBlocking

class NewMoneyMovingUseCase {

    fun addInDataBase(db: MoneyMovementDao, moneyMovement: MoneyMovement) {
        runBlocking {
            val result = db.addMovingMoney(moneyMovement)
            Log.i("TAG","---result inserting--- $result")
        }
    }

}