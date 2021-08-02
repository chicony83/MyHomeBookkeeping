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
        categoryId: Int,
        currencyId: Int,
        description: String

    ): Int {
        return db.changeMoneyMovingLine(
            id,
            dateTime,
            amount,
            cashAccountId,
            categoryId,
            currencyId,
            description
        )
    }
}
