package com.chico.myhomebookkeeping.ui.cashAccount.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import java.lang.IllegalStateException

class SelectCashAccountDialog(
    private val cashAccount: CashAccount?,
    private val onItemSelectForChangeCallBack: OnItemSelectForChangeCallBack,
    private val onItemSelectForSelectCallBackInt: OnItemSelectForSelectCallBackInt
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_cash_account, null)

            val name = layout.findViewById<TextView>(R.id.selectedItemName)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            cashAccount?.let { it1->name.text = it1.accountName }

            name.setOnClickListener {
                cashAccount?.cashAccountId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                dialogCancel()
            }
            selectButton.setOnClickListener {
                cashAccount?.cashAccountId?.let { it1 ->
                    onItemSelectForSelectCallBackInt.onSelect(it1)
                }
                dialogCancel()
            }
            changeButton.setOnClickListener {
                cashAccount?.cashAccountId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                dialogCancel()
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
}