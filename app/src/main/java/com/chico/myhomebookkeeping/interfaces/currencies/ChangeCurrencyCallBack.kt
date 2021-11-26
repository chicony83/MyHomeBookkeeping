package com.chico.myhomebookkeeping.interfaces.currencies

import com.chico.myhomebookkeeping.db.entity.Currencies

interface ChangeCurrencyCallBack {
    fun change(id:Int,name:String)
}