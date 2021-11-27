package com.chico.myhomebookkeeping.interfaces.currencies

interface OnAddNewCurrencyCallBack {
    fun add(name:String)
    fun addAndSelect(name: String)
}