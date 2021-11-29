package com.chico.myhomebookkeeping.interfaces.categories

interface OnChangeCategoryCallBack {
    fun change(id: Int,name:String,isIncome:Boolean)
}