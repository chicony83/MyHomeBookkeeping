package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object ChangeFastPaymentUseCase {
    suspend fun changeFastPayment(
        db: FastPaymentsDao,
        id: Long,
        name: String,
        rating: Int,
        cashAccount: Int,
        currencyId: Int,
        category: Int,
        amount: Double,
        description: String,
        childCategories: List<ChildCategory>
    ): Int {
        return db.changeFastPayment(
            id,
            name,
            rating,
            cashAccount,
            currencyId,
            category,
            amount,
            description,
            childCategories
        )

    }

    suspend fun deleteLine(db: FastPaymentsDao, id: Long): Int {
        return db.deleteLine(id)
    }
}