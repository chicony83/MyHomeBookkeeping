package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.interfaces.currencies.ChangeCurrencyCallBack
import java.lang.IllegalStateException

class ChangeCurrencyDialog(
    val currency: Currencies?,
    private val changeCurrencyCallBack: ChangeCurrencyCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_currency, null)

            val name = layout.findViewById<EditText>(R.id.name)

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            name.setText(currency?.currencyName.toString())

            saveButton.setOnClickListener {
                changeCurrencyCallBack.change(
                    id = currency?.currencyId ?: 0,
                    name = name.text.toString()
                )
                closeDialog()
            }

            cancelButton.setOnClickListener {
                closeDialog()
            }

            builder.setView(layout)
            builder.create()
        } ?: throw  IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun closeDialog() {
        dialog?.cancel()
    }
}