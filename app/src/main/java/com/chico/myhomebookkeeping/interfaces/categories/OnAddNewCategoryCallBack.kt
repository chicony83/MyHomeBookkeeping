package com.chico.myhomebookkeeping.interfaces.categories

interface OnAddNewCategoryCallBack {
//    fun add(name:String,isIncome:Boolean)
    fun addAndSelect(name: String,isIncome: Boolean,isSelect:Boolean)
}