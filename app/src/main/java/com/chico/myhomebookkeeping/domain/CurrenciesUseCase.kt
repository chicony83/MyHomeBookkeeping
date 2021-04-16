package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo

object CurrenciesUseCase {

    suspend fun getOneCurrency(db: CurrenciesDao,id:Int):String{
        var text = ""
        var result:List<Currencies>
        launchForResult {
            result = db.getOneCurrency(id)
            text = result.first().currencyName
        }
        return text
    }

    fun addNewCurrency(db: CurrenciesDao, addingCurrency: Currencies) {
        launchIo {
            db.addCurrency(addingCurrency)
        }
    }
}