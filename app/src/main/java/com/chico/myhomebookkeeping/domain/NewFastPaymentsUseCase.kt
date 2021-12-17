package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.entity.FastPayments

object NewFastPaymentsUseCase {

    suspend fun addNewFastPayment(db: FastPaymentsDao, newFastPayment: FastPayments): Long {
        return db.addBlank(newFastPayment)
    }
    suspend fun getAllBlanks(db: FastPaymentsDao):List<FastPayments>{
        return db.getAllBlanks()
    }
}