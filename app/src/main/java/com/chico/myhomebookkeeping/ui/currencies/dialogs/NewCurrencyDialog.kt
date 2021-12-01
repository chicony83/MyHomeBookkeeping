package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.EditNameTextWatcher
import java.lang.IllegalStateException
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.currencies.OnAddNewCurrencyCallBack
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.utils.getString


class NewCurrencyDialog(
    private val result: Any,
    private val onAddNewCurrencyCallBack: OnAddNewCurrencyCallBack
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_currency, null)

            var namesList = listOf<String>()

            val nameEditText = layout.findViewById<EditText>(R.id.currency_name)
            val errorTextView = layout.findViewById<TextView>(R.id.error_this_name_is_taken)

            val addButton = layout.findViewById<Button>(R.id.addNewCurrencyButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)

            if (result is List<*>) {
                namesList = (result as List<String>)
//                Message.log("names list size= ${namesList.size}")
            }
            fun listButtons() = listOf(
                addButton, addAndSelectButton
            )
            nameEditText.addTextChangedListener(
                EditNameTextWatcher(
                    namesList,
                    listButtons(),
                    errorTextView
                )
            )

            addAndSelectButton.setOnClickListener {
                val name = nameEditText.getString()
                if (nameEditText.text.isNotEmpty()) {
                    val isLengthChecked: Boolean = CheckString.isLengthMoThan(name)
                    if (isLengthChecked) {
                        onAddNewCurrencyCallBack.addAndSelect(name = name)
                        dialogCancel()
                    }
                    if (!isLengthChecked) {
                        showMessage(getString(R.string.message_too_short_name))
                    }
                } else if (nameEditText.text.isEmpty()) {
                    showMessage(getString(R.string.message_too_short_name))
                }
            }

            addButton.setOnClickListener {
                val name = nameEditText.getString()
                if (name.isNotEmpty()) {
                    val isLengthChecked: Boolean = CheckString.isLengthMoThan(name)
                    if (isLengthChecked) {
                        onAddNewCurrencyCallBack.add(name)
                        dialogCancel()
                    }
                    if (!isLengthChecked) {
                        showMessage(getString(R.string.message_too_short_name))
                    }
                } else if (name.isEmpty()) {
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

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}