package com.chico.myhomebookkeeping.interfaces.categories

interface OnAddNewCategoryCallBack {
//    fun add(name:String,isIncome:Boolean)
    fun addAndSelectWithoutParentCategory(name: String, isIncome: Boolean, isSelect: Boolean, icon: Int)
    fun addAndSelectFull(name: String,parentCategoryId:Int, isIncome: Boolean, isSelect: Boolean, icon: Int)
    
}