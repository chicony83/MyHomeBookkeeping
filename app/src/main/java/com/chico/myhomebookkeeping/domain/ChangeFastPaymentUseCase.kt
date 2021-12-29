package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object ChangeFastPaymentUseCase {
    suspend fun changeFastPayment(
        db: FastPaymentsDao,
        id: Long,
        name: String,
        rating: Int,
        cashAccount: Int,
        currency: Int,
        category: Int,
        amount: Double,
        description: String
    ): Int {
        return db.changeFastPayment(
            id,
            name,
            rating,
            cashAccount,
            currency,
            category,
            amount,
            description
        )

    }
}