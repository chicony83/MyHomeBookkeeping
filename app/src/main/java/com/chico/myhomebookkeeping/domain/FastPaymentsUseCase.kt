package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.entity.FastPayments

object FastPaymentsUseCase {

    suspend fun addNewBlank(db: FastPaymentsDao, newMy: FastPayments): Long {
        return db.addBlank(newMy)
    }
    suspend fun getAllBlanks(db: FastPaymentsDao):List<FastPayments>{
        return db.getAllBlanks()
    }
}