package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

object NewMoneyMovementUseCase {
    suspend fun addInDataBase(db: MoneyMovementDao, moneyMovement: MoneyMovement): Long {
        return db.addMovingMoney(moneyMovement)
    }
}