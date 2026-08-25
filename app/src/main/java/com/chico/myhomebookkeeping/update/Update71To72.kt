package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.helpers.Message

class Update71To72 {
    suspend fun update(app: Application) {
        Message.log("...updating 71 to 72...")

        val categoryDb = dataBase.getDataBase(app.applicationContext).categoryDao()
        categoryDb.rebuildUsageCount()
        categoryDb.clearTransferFeeUsageCount()

        Message.log("...updating 71 ne 72 complete...")
    }
}
