package com.chico.myhomebookkeeping.interfaces.currencies

interface OnChangeCurrencyCallBack {
    fun change(id:Int,name:String)
    fun change(id:Int,name:String,nameShort:String?,iSO:String?)

}