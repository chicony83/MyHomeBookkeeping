package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

object NewMoneyMovementUseCase {
    suspend fun addInDataBase(db: MoneyMovementDao, moneyMovement: MoneyMovement): Long {
        return db.addMovingMoney(moneyMovement)
    }

    suspend fun addTransferInDataBase(
        db: MoneyMovementDao,
        source: MoneyMovement,
        destination: MoneyMovement
    ): List<Long> {
        return db.addTransfer(source, destination)
    }
}
