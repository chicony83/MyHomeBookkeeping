package com.chico.myhomebookkeeping.ui.cashAccount.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.interfaces.cashAccounts.OnChangeCashAccountCallBack
import java.lang.IllegalStateException

class ChangeCashAccountDialog(
    private val cashAccount: CashAccount?,
    private val onChangeCashAccountCallBack: OnChangeCashAccountCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_cash_account, null)

            val nameEditText = layout.findViewById<EditText>(R.id.name)
            val numberEditText = layout.findViewById<EditText>(R.id.number)

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            cashAccount?.let { it1 -> nameEditText.setText(it1.accountName.toString()) }

            if (CheckString.isLengthMoThan(cashAccount?.bankAccountNumber.toString(), 0)) {
                cashAccount?.let { it1 -> numberEditText.setText(it1.bankAccountNumber.toString()) }
            }

            saveButton.setOnClickListener {
                if (nameEditText.text.isNotEmpty()){
                    onChangeCashAccountCallBack.change(
                        id =  cashAccount?.cashAccountId?:0,
                        name = nameEditText.text.toString(),
                        number = numberEditText.text.toString()
                    )
                    dialogCancel()
                }else if(nameEditText.text.isEmpty()){
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