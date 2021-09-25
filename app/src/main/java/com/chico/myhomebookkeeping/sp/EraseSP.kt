package com.chico.myhomebookkeeping.sp

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.obj.Constants

class EraseSP(val spEditor: SharedPreferences.Editor) {
    fun eraseTempSP(){
        eraseAmountCreateSP()
    }

    private fun eraseAmountCreateSP() {
        spEditor.remove(Constants.FOR_CREATE_AMOUNT_KEY).commit()
    }
}