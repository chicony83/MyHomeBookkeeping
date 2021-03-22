package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.utils.launchIo

class CurrenciesUseCase {
    fun addNewCurrency(db: CurrenciesDao, addingCurrency: Currencies) {
        launchIo {
            db.addCurrency(addingCurrency)
        }
    }
}