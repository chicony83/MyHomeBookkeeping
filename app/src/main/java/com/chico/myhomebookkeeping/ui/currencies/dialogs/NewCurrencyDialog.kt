package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.textWathers.EditNameTextWatcher
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

            val nameEditText = layout.findViewById<EditText>(R.id.currencyNameEditText)
            val shortNameEditText = layout.findViewById<EditText>(R.id.currencyNameShortEditText)
            val iSOEditText = layout.findViewById<EditText>(R.id.currencyNameISOEditText)
            val errorTextView = layout.findViewById<TextView>(R.id.errorThisNameIsTaken)

            val addButton = layout.findViewById<Button>(R.id.addNewCurrencyButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)

            if (result is List<*>) {
                namesList = (result as List<String>)
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
                checkAndAddCurrency(
                    nameEditText,
                    shortNameEditText,
                    iSOEditText,
                    isSelectAfterAdd = true
                )
            }

            addButton.setOnClickListener {
                checkAndAddCurrency(
                    nameEditText,
                    shortNameEditText,
                    iSOEditText,
                    isSelectAfterAdd = false
                )
            }

            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun checkAndAddCurrency(
        nameEditText: EditText,
        shortNameEditText: EditText,
        iSOEditText: EditText,
        isSelectAfterAdd: Boolean
    ) {
        val nameCurrency = nameEditText.getString()
        val nameShortCurrency = shortNameEditText.getString()
        val iSOCurrency = iSOEditText.getString()

        if (nameCurrency.isNotEmpty()) {
            val isLengthNameChecked: Boolean = CheckString.isLengthMoThan(nameCurrency)
//            val isLengthNameShortChecked: Boolean = CheckString.isLengthMoThan(nameCurrency)
//            val isLengthISOChecked: Boolean = CheckString.isLengthMoThan(nameCurrency)
            if (isLengthNameChecked) {

                onAddNewCurrencyCallBack.addAndSelect(nameCurrency, nameShortCurrency,iSOCurrency, isSelectAfterAdd)
                dialogCancel()
            }
            if (!isLengthNameChecked) {
                showMessage(getString(R.string.message_too_short_name))
            }
        } else if (nameCurrency.isEmpty()) {
            showMessage(getString(R.string.message_too_short_name))
        }
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}