package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.ui.cashAccount.CashAccountViewModel
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.runBlocking

class CashAccountsUseCase {
//    fun addNewCashAccount(db: CashAccountDao, newCashAccount: CashAccount) {
//        launchIo {
//            db.addCashAccount(newCashAccount)
//        }
//    }

    suspend fun getOneCashAccount(db: CashAccountDao, id: Int): String {
        var text = ""
        launchForResult {
            val result: List<CashAccount> = db.getCashAccount(id)
            text = result.first().accountName
        }
        return text
    }

    fun addCashRunBlocking(
        db: CashAccountDao,
        newCashAccount: CashAccount,
        cashAccountViewModel: CashAccountViewModel
    ) = runBlocking {
        db.addCashAccount(newCashAccount)
        cashAccountViewModel.loadCashAccounts()
    }

}