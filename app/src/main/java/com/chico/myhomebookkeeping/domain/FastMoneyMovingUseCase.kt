package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.FastMoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.FastMoneyMovement

object FastMoneyMovingUseCase {

    suspend fun addNewBlank(db: FastMoneyMovementDao, newFast: FastMoneyMovement): Long {
        return db.addBlank(newFast)
    }
    suspend fun getAllBlanks(db: FastMoneyMovementDao):List<FastMoneyMovement>{
        return db.getAllBlanks()
    }
}