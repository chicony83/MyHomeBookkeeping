package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo

class CashAccountsUseCase {
    fun addNewCashAccount(db: CashAccountDao, newCashAccount: CashAccount) {
        launchIo {
            db.addCashAccount(newCashAccount)
        }
    }
    suspend fun getCashAccount(db:CashAccountDao, id:Int):String{
        var text = ""
        launchForResult {
            val result: List<CashAccount> = db.getCashAccount(id)
            text = result.first().accountName

        }
        return text
    }
}