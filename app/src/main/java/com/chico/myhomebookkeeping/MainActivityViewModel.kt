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

    private val dbCategory: CategoryDao = dataBase.getDataBase(app.applicationContext).categoryDao()
    private val spName = Constants.SP_NAME
    private var sharedPreferences: SharedPreferences = app.getSharedPreferences(spName,Context.MODE_PRIVATE)
    private var getSP = GetSP(sharedPreferences)
    private var setSP = SetSP(sharedPreferences.edit())

    fun checkIsFirstLaunch() {
        Message.log("---is first launch = ${getSP.getBoolean(Constants.IS_FIRST_LAUNCH)}")
        if (getSP.getBoolean(Constants.IS_FIRST_LAUNCH)){
            setSP.saveToSP(Constants.IS_FIRST_LAUNCH, false)

        }
    }

}