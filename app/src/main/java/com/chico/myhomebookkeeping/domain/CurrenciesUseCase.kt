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

    fun addNewCurrency(
        db: CurrenciesDao,
        newCurrency: Currencies,
    ) = runBlocking {
        db.addCurrency(newCurrency)
    }

    fun changeCurrencyLine(db: CurrenciesDao, id:Int, name: String) = launchIo {
        db.changeLine(id,name)
    }
}