package com.chico.myhomebookkeeping.domain

import androidx.room.Dao
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchForResult

object NewMoneyMovingUseCase {
    suspend fun getOneString(dbCashAccount: CashAccountDao, id: Int): List<CashAccount>? {
        return launchForResult {
            listOf(dbCashAccount.getOneCashAccount(id).first())
        }
    }

}