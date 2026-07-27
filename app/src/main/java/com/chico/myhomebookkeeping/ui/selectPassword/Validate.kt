package com.chico.myhomebookkeeping.ui.selectPassword

import com.chico.myhomebookkeeping.R
import java.util.regex.Pattern

class Validate(
    private val viewModel: SelectPasswordViewModel,
    private val getString: (Int) -> String
) {
    fun validate(passwordString: String, repeatPasswordString: String): Boolean {
        val uppercase = Pattern.compile("[A-Z]")
        val lowercase = Pattern.compile("[a-z]")
        val digit = Pattern.compile("[0-9]")

        if (passwordString != repeatPasswordString) {
            setMessage(getString(R.string.message_passwords_do_not_match))
            return false
        }
        if (passwordString.length > 3) {
            if (passwordString == repeatPasswordString) {
                setMessage(getString(R.string.message_passwords_match))
                return true
            }
        } else {
            setMessage(getString(R.string.message_password_too_short))
            return false
        }
        return false
    }

    private fun setMessage(text: String) {
        viewModel.setPasswordMessage(text)
//        passwordMessage.text = text
    }
}
