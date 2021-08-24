package com.chico.myhomebookkeeping.domain

import android.util.Log
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.ui.cashAccount.CashAccountViewModel
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo
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

    suspend fun addNewCashAccount(
        db: CashAccountDao,
        newCashAccount: CashAccount,
    ): Long {
        return db.addCashAccount(newCashAccount)
    }

    suspend fun changeCashAccountLine(
        db: CashAccountDao,
        id: Int,
        name: String,
        number: String
    ) :Int {
        Log.i("TAG", " fun changeCashAccountLine name = $name, number = $number")
        return db.changeLine(id = id, name = name, number = number)
    }

}