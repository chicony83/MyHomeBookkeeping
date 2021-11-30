package com.chico.myhomebookkeeping.interfaces.cashAccounts

interface OnAddNewCashAccountsCallBack {
    fun addAndSelect(name:String, number:String, isSelect:Boolean)
}