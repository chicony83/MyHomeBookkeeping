package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.ui.cashAccount.CashAccountViewModel
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object CashAccountsUseCase {

//    suspend fun getOneCashAccountName(db: CashAccountDao, id: Int): String {
//        return launchForResult {
//            db.getOneCashAccount(id).first().accountName
//        }.toString()
//    }

    suspend fun getOneCashAccount(db: CashAccountDao, id: Int): CashAccount? {
        return launchForResult {
            db.getOneCashAccount(id)
        }
    }

    fun addNewCashAccountRunBlocking(
        db: CashAccountDao,
        newCashAccount: CashAccount,
        cashAccountViewModel: CashAccountViewModel
    ) = runBlocking {
        db.addCashAccount(newCashAccount)
        cashAccountViewModel.loadCashAccounts()
    }

}