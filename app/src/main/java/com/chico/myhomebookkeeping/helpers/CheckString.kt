package com.chico.myhomebookkeeping.helpers

import com.chico.myhomebookkeeping.obj.Constants

object CheckString {
    private val minLength: Int = Constants.MIN_LENGTH_NAME
    fun isLengthMoThan(text:String):Boolean{
        return text.length > minLength
    }

    fun isLengthMoThan(text: String, length: Int): Boolean {
        return text.length>length
    }
}