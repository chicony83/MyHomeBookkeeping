package com.chico.myhomebookkeeping.interfaces.cashAccounts

interface OnChangeCashAccountCallBack {
    fun change(id:Int,name:String,number:String)
}