package com.chico.myhomebookkeeping.domain

import android.content.SharedPreferences
import com.chico.myhomebookkeeping.obj.Constants

class PasswordsUseCase(val sharedPreferences: SharedPreferences) {

    private val spPass = Constants.SP_PASS
    private val spPrompt  = Constants.SP_PASS_PROMPT
    private val spEditor = sharedPreferences.edit()

    fun savePassword(pass: String) {
        saveToSp(spPass,pass)
    }

    fun getPrompt(): String? {
        return sharedPreferences.getString(spPrompt,"")
    }

    fun savePrompt(prompt: String) {
        saveToSp(spPrompt,prompt)
    }
    private fun saveToSp(spName: String, text: String) {
        spEditor.putString(spName,text)
        spEditor.commit()
    }

    fun getPass(): String? {
        return sharedPreferences.getString(spPass,"")
    }
}