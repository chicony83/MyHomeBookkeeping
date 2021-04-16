package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.ui.cashAccount.CashAccountViewModel
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.runBlocking

object CashAccountsUseCase {



    suspend fun getOneCashAccount(db: CashAccountDao, id: Int): String {
        var text: String = ""
        var result: List<CashAccount>
        launchForResult {
            result = db.getOneCashAccount(id)
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