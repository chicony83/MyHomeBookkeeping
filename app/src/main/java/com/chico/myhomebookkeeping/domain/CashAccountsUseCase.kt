package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchForResult

object CashAccountsUseCase {
    suspend fun addNewCashAccount(
        db: CashAccountDao,
        newCashAccount: CashAccount,
    ): Long {
        return db.addCashAccount(newCashAccount)
    }

    suspend fun getOneCashAccountById(
        db: CashAccountDao,
        id: Int
    ): CashAccount? {
        return launchForResult {
            db.getOneCashAccount(id)
        }
    }

    suspend fun changeCashAccountLine(
        db: CashAccountDao,
        id: Int,
        name: String,
        number: String
    ): Int {
        return db.changeLine(id = id, name = name, number = number)
    }

    suspend fun getAllCashAccountsSortNameAsc(db: CashAccountDao): List<CashAccount> {
        return db.getAllCashAccountsSortNameAsc()
    }
    suspend fun getAllCashAccountsSortIdAsc(db:CashAccountDao):List<CashAccount>{
        return db.getAllCashAccountsSortIdAsc()
    }
}