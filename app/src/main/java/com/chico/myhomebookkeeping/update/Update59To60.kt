package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.helpers.Message

class Update59To60 {
    suspend fun update(app: Application) {
        Message.log("...updating 59 to 60...")

        val cashAccountDb: CashAccountDao =
            dataBase.getDataBase(app.applicationContext).cashAccountDao()
        val currenciesDb: CurrenciesDao =
            dataBase.getDataBase(app.applicationContext).currenciesDao()

        setDefaultCashAccountIfNeeded(cashAccountDb)
        setDefaultCurrencyIfNeeded(currenciesDb)

        Message.log("...updating 59 ne 60 complete...")
    }

    private suspend fun setDefaultCashAccountIfNeeded(cashAccountDb: CashAccountDao) {
        if (cashAccountDb.getDefaultCashAccount() != null) return

        cashAccountDb.getFirstCashAccount()?.cashAccountId?.let {
            CashAccountsUseCase.setDefaultCashAccount(cashAccountDb, it)
        }
    }

    private suspend fun setDefaultCurrencyIfNeeded(currenciesDb: CurrenciesDao) {
        if (currenciesDb.getDefaultCurrency() != null) return

        currenciesDb.getFirstCurrency()?.currencyId?.let {
            CurrenciesUseCase.setDefaultCurrency(currenciesDb, it)
        }
    }
}
