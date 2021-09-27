package com.chico.myhomebookkeeping.domain

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.launchForResult

object MoneyMovingUseCase {
    suspend fun getOneMoneyMoving(db: MoneyMovementDao, id: Long): MoneyMovement? {
        return launchForResult {
            db.getOneMoneyMoving(id = id)
        }
    }

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

    suspend fun getSelectedMoneyMovement(
        db: MoneyMovementDao,
        query: SimpleSQLiteQuery
    ): List<MoneyMovement>? {
        return launchForResult {
            db.getSelectedMoneyMoving(query)
        }
    }

    suspend fun getOneFullMoneyMoving(
        db: MoneyMovementDao,
        query: SimpleSQLiteQuery
    ): FullMoneyMoving? {
        return launchForResult {
            db.getOneFullMoneyMoving(query = query)
        }
    }

    suspend fun getSelectedFullMoneyMovement(
        db: MoneyMovementDao,
        query: SimpleSQLiteQuery
    ): List<FullMoneyMoving>? {
        return launchForResult {
            db.getSelectedFullMoneyMoving(query)
        }
    }
}

