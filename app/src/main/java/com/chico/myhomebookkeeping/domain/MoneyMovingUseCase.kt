package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchForResult

object MoneyMovingUseCase {
    suspend fun getMoneyMovement(db: MoneyMovementDao): List<MoneyMovement>? {
        return launchForResult {
            db.getAllMovingMoney()
        }
    }
}