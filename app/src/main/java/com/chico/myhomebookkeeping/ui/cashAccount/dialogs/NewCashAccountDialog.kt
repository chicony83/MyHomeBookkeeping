package com.chico.myhomebookkeeping.ui.cashAccount.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.EditNameTextWatcher
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.interfaces.cashAccounts.OnAddNewCashAccountsCallBack
import com.chico.myhomebookkeeping.utils.getString
import java.lang.IllegalStateException

class NewCashAccountDialog(
    private val result: Any,
    private val onAddNewCashAccountsCallBack: OnAddNewCashAccountsCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_cash_account, null)

            var namesList = listOf<String>()

            val nameEditText = layout.findViewById<EditText>(R.id.name)
            val numberEditText = layout.findViewById<EditText>(R.id.number)
            val errorTextView = layout.findViewById<TextView>(R.id.error_this_name_is_taken)

            val addButton = layout.findViewById<Button>(R.id.addNewCashAccountButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)

            if (result is List<*>) {
                namesList = (result as List<String>)
            }

            fun buttonsList() = listOf(
                addButton, addAndSelectButton
            )
            nameEditText.addTextChangedListener(
                EditNameTextWatcher(
                    namesList = namesList,
                    buttonList = buttonsList(),
                    errorMessageTexView = errorTextView
                )
            )

            addAndSelectButton.setOnClickListener {
                checkAndAddCashAccount(
                    nameEditText,
                    numberEditText,
                    isSelectAfterAdd = true
                )
            }
            addButton.setOnClickListener {
                checkAndAddCashAccount(
                    nameEditText,
                    numberEditText,
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

    private fun checkAndAddCashAccount(
        nameEditText: EditText,
        numberEditText: EditText,
        isSelectAfterAdd: Boolean
    ) {
        val name = nameEditText.getString()
        val number = numberEditText.getString()
        if (nameEditText.text.isNotEmpty()){
            val isLengthChecked = CheckString.isLengthMoThan(name)
//            val isNumberEntered:Boolean = CheckString.isLengthMoThan(number,0)
            if (isLengthChecked){
                onAddNewCashAccountsCallBack.addAndSelect(
                    name = name,
                    number = number,
                    isSelectAfterAdd
                )
                dialogCancel()
            }else if (!isLengthChecked){
                showMessage(getString(R.string.message_too_short_name))
            }
        } else if(nameEditText.text.isEmpty()){
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