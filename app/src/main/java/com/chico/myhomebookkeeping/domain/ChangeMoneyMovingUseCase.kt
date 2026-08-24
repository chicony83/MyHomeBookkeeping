package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement

object ChangeMoneyMovingUseCase {
    suspend fun changeMoneyMovingLine(
        db: MoneyMovementDao,
        id: Long,
        dateTime: Long,
        amount: Double,
        cashAccountId: Int,
        categoryId: Int?,
        paymentTypeId: Int,
        currencyId: Int,
        description: String

    ): Int {
        return db.changeMoneyMovingLine(
            id,
            dateTime,
            amount,
            cashAccountId,
            categoryId,
            paymentTypeId,
            currencyId,
            description
        )
    }

    suspend fun deleteLine(db: MoneyMovementDao, id: Long): Int {
       return db.deleteLine(id)
    }

    suspend fun getTransferRows(db: MoneyMovementDao, transferGroupId: Long): List<MoneyMovement> {
        return db.getTransferRows(transferGroupId)
    }

    suspend fun changeTransferLine(
        db: MoneyMovementDao,
        id: Long,
        dateTime: Long,
        amount: Double,
        cashAccountId: Int,
        currencyId: Int,
        description: String
    ): Int {
        return db.changeTransferLine(
            id,
            dateTime,
            amount,
            cashAccountId,
            currencyId,
            description
        )
    }

    suspend fun deleteTransferRows(db: MoneyMovementDao, transferGroupId: Long): Int {
        return db.deleteTransferRows(transferGroupId)
    }
}
