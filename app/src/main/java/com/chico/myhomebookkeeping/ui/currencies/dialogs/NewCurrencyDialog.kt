package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.helpers.Message
import java.lang.IllegalStateException
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.utils.getString


class NewCurrencyDialog() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_currency, null)

            val addButton = layout.findViewById<Button>(R.id.addNewCurrencyButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)
            val editText = layout.findViewById<EditText>(R.id.currency_name)

            addAndSelectButton.setOnClickListener {
                val text = editText.getString()
                if (!text.isNullOrEmpty()) {
                    notEmptyNameMessage(editText)
                }
                else if (text.isNullOrEmpty()) {
                    emptyNameMessage()
                }

                Message.log("---Add and select Clicked---")
            }
            addButton.setOnClickListener {
                val text = editText.getString()
                if (!text.isNullOrEmpty()) {
                    notEmptyNameMessage(editText)

                }
                if (text.isNullOrEmpty()) {
                    emptyNameMessage()
                }
            }
            cancelButton.setOnClickListener {
                dialog?.cancel()
            }
            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun notEmptyNameMessage(editText: EditText) {
        Message.log("name = ${editText.text}")
    }

    private fun emptyNameMessage() {
        Message.log("name in empty")
    }
}