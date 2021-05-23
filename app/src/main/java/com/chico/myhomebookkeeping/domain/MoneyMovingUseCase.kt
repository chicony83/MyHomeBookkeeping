package com.chico.myhomebookkeeping.domain

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchForResult

object MoneyMovingUseCase {
    suspend fun getMoneyMovement(db: MoneyMovementDao): List<MoneyMovement>? {
        return launchForResult {
            db.getAllMovingMoney()
        }
    }
    suspend fun getFullMoneyMovement(db: MoneyMovementDao): List<FullMoneyMoving>? {
        return launchForResult {
            db.getFullMoneyMoving()
        }
    }

    suspend fun getSelectedMoneyMovement(db: MoneyMovementDao,query: SimpleSQLiteQuery): List<FullMoneyMoving>?{
        return launchForResult {
            db.getSelectedMoneyMoving(query)
        }
    }
}