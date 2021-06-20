package com.chico.myhomebookkeeping.ui.selectPassword

import java.util.regex.Pattern

class Validate(private val viewModel: SelectPasswordViewModel) {
    fun validate(passwordString: String, repeatPasswordString: String): Boolean {
        val uppercase = Pattern.compile("[A-Z]")
        val lowercase = Pattern.compile("[a-z]")
        val digit = Pattern.compile("[0-9]")

        if (passwordString != repeatPasswordString){
            setMessage("пароли не совпадают")
            return false
        }
        if (passwordString.length > 3) {
            if (passwordString == repeatPasswordString){
                setMessage("пароль совпадает")
                return true
            }
        } else {
            setMessage("слишком короткий пароль")
            return false
        }
        return false
    }

    private fun setMessage(text: String) {
        viewModel.setPasswordMessage(text)
//        passwordMessage.text = text
    }

}