package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.BlankMoneyMovementDao
import com.chico.myhomebookkeeping.db.entity.BlankMoneyMovement

object BlankMoneyMovingUseCase {

    suspend fun addNewBlank(db: BlankMoneyMovementDao, newBlank: BlankMoneyMovement): Long {
        return db.addBlank(newBlank)
    }
    suspend fun getAllBlanks(db: BlankMoneyMovementDao):List<BlankMoneyMovement>{
        return db.getAllBlanks()
    }
}