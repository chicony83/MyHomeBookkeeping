package com.chico.myhomebookkeeping.domain

import android.util.Log
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object NewMoneyMovingUseCase {

    suspend fun addInDataBase(db: MoneyMovementDao, moneyMovement: MoneyMovement): Long {

        return db.addMovingMoney(moneyMovement)
    }
}
