package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.EditNameTextWatcher
import java.lang.IllegalStateException
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.addItems.AddNewCurrencyCallBack
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.utils.getString


class NewCurrencyDialog(val result: Any, private val addNewCurrencyCallBack: AddNewCurrencyCallBack) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_currency, null)

            val addButton = layout.findViewById<Button>(R.id.addNewCurrencyButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)
            val editText = layout.findViewById<EditText>(R.id.currency_name)
            val errorTextView = layout.findViewById<TextView>(R.id.error_this_name_is_taken)
            var namesList= mutableListOf<String>()

            if (result is List<*>){
                namesList = (result as List<String>).toMutableList()
            }
            editText.addTextChangedListener(EditNameTextWatcher(namesList,addButton,errorTextView))
            addAndSelectButton.setOnClickListener {
                val text = editText.getString()
                if (!text.isNullOrEmpty()) {
                    val isLengthChecked: Boolean = checkLengthText(text)
                    if (isLengthChecked){
                        addNewCurrencyCallBack.add(text)
                        dialogCancel()
                    }
                    if (!isLengthChecked){
                        showMessage(getString(R.string.message_too_short_name))
                    }
                }
                else if (text.isNullOrEmpty()) {
                    showMessage(getString(R.string.message_too_short_name))
                }
            }
            addButton.setOnClickListener {
                val text = editText.getString()
                if (!text.isNullOrEmpty()) {
                    val isLengthChecked: Boolean = checkLengthText(text)
                    if (isLengthChecked){
                        addNewCurrencyCallBack.add(text)
                        dialogCancel()
                    }
                    if (!isLengthChecked){
                        showMessage(getString(R.string.message_too_short_name))
                    }
                }
                else if (text.isNullOrEmpty()) {
                    showMessage(getString(R.string.message_too_short_name))
                }
            }
            cancelButton.setOnClickListener {
                dialogCancel()
            }
            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun checkLengthText(text: String): Boolean {
        return CheckString.isLengthMoThan(text)
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}