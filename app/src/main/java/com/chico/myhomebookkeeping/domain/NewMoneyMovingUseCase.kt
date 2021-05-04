package com.chico.myhomebookkeeping.domain

import android.util.Log
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

class NewMoneyMovingUseCase {

    suspend fun addInDataBase(db: MoneyMovementDao, moneyMovement: MoneyMovement): Long {

        return db.addMovingMoney(moneyMovement)
//        return adding(db, moneyMovement)
    }

//    private suspend fun adding(db: MoneyMovementDao, moneyMovement: MoneyMovement): Long {
//
//            return db.addMovingMoney(moneyMovement)
//
//    }

}
