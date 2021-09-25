package com.chico.myhomebookkeeping.ui.selectPassword

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.domain.PasswordsUseCase

class SelectPasswordViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val spName = Constants.SP_NAME

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)

    private val passwordsUseCase = PasswordsUseCase(sharedPreferences)

//    private var _selectPassword = MutableLiveData<String>()
//    val selectPassword: LiveData<String>
//        get() = _selectPassword

    private var _passwordMessage = MutableLiveData<String>()
    val passwordMessage: LiveData<String>
        get() = _passwordMessage

    private var _promptPass = MutableLiveData<String>()
    val promptPass: LiveData<String>
        get() = _promptPass

    init {
        _promptPass.postValue(
            passwordsUseCase.getPrompt()
        )
    }

//    fun getPassword(): String {
//        return passwordsUseCase.getPass().toString()
//    }

    fun setPasswordMessage(text: String) {
        _passwordMessage.postValue(text)
    }

    private fun savePassword(pass: String) {
        passwordsUseCase.savePassword(pass)
    }

    fun save(pass: String, prompt: String) {
        savePassword(pass)
        savePrompt(prompt)
    }

    private fun savePrompt(prompt: String) {
        passwordsUseCase.savePrompt(prompt)
    }
}