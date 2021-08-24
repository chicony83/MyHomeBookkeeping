package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo
import kotlinx.coroutines.runBlocking

object CurrenciesUseCase {

    suspend fun getOneCurrency(db: CurrenciesDao, id: Int): Currencies? {
        return launchForResult {
            db.getOneCurrency(id)
        }
    }

    suspend fun addNewCurrency(
        db: CurrenciesDao,
        newCurrency: Currencies,
    ) :Long {
        return db.addCurrency(newCurrency)
    }

    suspend fun changeCurrencyLine(db: CurrenciesDao, id:Int, name: String) :Int {
        return db.changeLine(id,name)
    }
}