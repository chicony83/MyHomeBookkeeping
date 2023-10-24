package com.chico.myhomebookkeeping.interfaces.categories

interface OnChangeCategoryCallBack {
//    fun changeWithoutIcon(id: Int, name:String, isIncome:Boolean)
    fun changeCategoryWithoutParentCategory(id:Int, name:String, isIncome: Boolean, iconResource:Int)
    fun changeCategoryFull(id:Int, name:String, isIncome: Boolean, iconResource:Int,parentCategoryId:Int)

}