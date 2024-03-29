package com.chico.myhomebookkeeping.domain

import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.utils.launchForResult

object FastPaymentsUseCase {

    suspend fun addNewFastPayment(db: FastPaymentsDao, newFastPayment: FastPayments): Long {
        return db.addBlank(newFastPayment)
    }

    suspend fun getListFastPayments(db: FastPaymentsDao): List<FastPayments> {
        return db.getAllFastPayments()
    }

    suspend fun getListFullFastPayments(
        db: FastPaymentsDao,
        query: SimpleSQLiteQuery
    ): List<FullFastPayment>? {
        return launchForResult {
            db.getAllFullFastPayments(query)
        }
    }

    suspend fun getOneFullFastPayment(
        db: FastPaymentsDao,
        query: SimpleSQLiteQuery
    ): FullFastPayment {
        return db.getOneFullFastPayment(query)
    }

    suspend fun getOneFastPayment(id: Long, db: FastPaymentsDao): FastPayments? {
        return launchForResult {
            db.getOneSelectedFastPayment(id)
        }
    }
}