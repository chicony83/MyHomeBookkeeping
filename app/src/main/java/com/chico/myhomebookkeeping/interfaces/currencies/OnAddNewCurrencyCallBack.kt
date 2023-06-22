package com.chico.myhomebookkeeping.interfaces.currencies

interface OnAddNewCurrencyCallBack {
//    fun add(name:String)
    fun addAndSelect(name: String,isSelect:Boolean)
    fun addAndSelect(currencyName:String, currencyShortName:String, currencyISO:String, isSelect: Boolean)

}