package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchIo

class CashAccountsUseCase {
    fun addNewCashAccount(db: CashAccountDao, newCashAccount: CashAccount) {
        launchIo {
            db.addCashAccount(newCashAccount)
        }
    }
}