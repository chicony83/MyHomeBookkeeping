package com.chico.myhomebookkeeping.sp

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.obj.Constants

class EraseSP(val spEditor: SharedPreferences.Editor) {
    fun eraseTempSP(){
        eraseAmountCreateSP()
    }

    private fun eraseAmountCreateSP() {
        spEditor.remove(Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY).commit()
    }
}