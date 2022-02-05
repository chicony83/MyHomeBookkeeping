package com.chico.myhomebookkeeping.interfaces.categories

interface OnChangeCategoryCallBack {
//    fun changeWithoutIcon(id: Int, name:String, isIncome:Boolean)
    fun changeWithIcon(id:Int,name:String,isIncome: Boolean,iconResource:Int)
}