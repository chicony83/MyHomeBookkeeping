package com.chico.myhomebookkeeping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP

class MainActivityViewModel(
    val app:Application
):AndroidViewModel(app) {

    private val spName = Constants.SP_NAME
    private var sharedPreferences: SharedPreferences = app.getSharedPreferences(spName,Context.MODE_PRIVATE)
    private var getSP = GetSP(sharedPreferences)

    fun checkIsFirstLaunch(): Boolean {
        Message.log("---is first launch = ${getSP.getBoolean(Constants.IS_FIRST_LAUNCH)}")
        return getSP.getBoolean(Constants.IS_FIRST_LAUNCH)
    }

}