package com.chico.myhomebookkeeping.interfaces.categories

interface OnAddNewCategoryCallBack {
//    fun add(name:String,isIncome:Boolean)
    fun addAndSelectWithoutParentCategory(name: String, isIncome: Boolean, isSelect: Boolean, iconKey: String)
    fun addAndSelectFull(name: String,parentCategoryId:Int, isIncome: Boolean, isSelect: Boolean, iconKey: String)
    
}
