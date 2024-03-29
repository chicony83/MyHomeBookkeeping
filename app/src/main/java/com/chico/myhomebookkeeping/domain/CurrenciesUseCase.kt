package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.utils.launchForResult

object CurrenciesUseCase {

    suspend fun addNewCurrency(
        db: CurrenciesDao,
        newCurrency: Currencies,
    ): Long {
        return db.addCurrency(newCurrency)
    }

    suspend fun addCurrencies(
        db: CurrenciesDao,
        currencies: List<Currencies>,
    ) = db.addCurrencies(currencies)

    suspend fun getOneCurrency(
        db: CurrenciesDao, id: Int
    ): Currencies? {
        return launchForResult {
            db.getOneCurrency(id)
        }
    }

    suspend fun changeCurrencyLine(
        db: CurrenciesDao,
        id: Int,
        name: String
    ): Int {
        return db.changeLine(id, name)
    }
    suspend fun changeCurrencyLineNameShortNameIso(
        db: CurrenciesDao,
        id: Int,
        name: String,
        nameShort:String?,
        iSO:String?
    ): Int {
        return db.changeLineCurrencyNameShortNameIso(
            id = id,
            name = name,
            shortName = nameShort,
            iSO = iSO
        )
    }

    suspend fun getAllCurrenciesSortNameAsc(
        db: CurrenciesDao
    ): List<Currencies> {
        return db.getAllCurrenciesSortNameAsc()
    }
}