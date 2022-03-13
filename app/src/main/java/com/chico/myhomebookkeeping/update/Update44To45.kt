package com.chico.myhomebookkeeping.update

import android.app.Application
import com.chico.myhomebookkeeping.helpers.Message

class Update44To45 {
    fun update(app: Application) {
        Message.log("...updating 44 to 45...")

        addIconsInDb()

        Message.log("...updating 44 ne 45 complete...")
    }

    private fun addIconsInDb() {

    }
}