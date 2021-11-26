package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemSelectForSelectCallBack
import com.chico.myhomebookkeeping.`interface`.OnItemSelectedForChangeCallBack
import com.chico.myhomebookkeeping.db.entity.Currencies
import java.lang.IllegalStateException

class SelectCurrencyDialog(
    val currencies: Currencies?,
    private val onItemSelectedForChangeCallBack: OnItemSelectedForChangeCallBack,
    private val onItemSelectForSelectCallBack: OnItemSelectForSelectCallBack
) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_currency, null)

            val name = layout.findViewById<TextView>(R.id.selectedItemName)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            currencies?.let { it1 -> name.text = it1.currencyName }
//            name.text = currencies?.currencyName
            name.setOnClickListener {
                currencies?.currencyId?.let { it1 ->
                    onItemSelectedForChangeCallBack.onSelect(it1)
                }
            }
            selectButton.setOnClickListener {
                currencies?.currencyId?.let { it1 ->
                    onItemSelectForSelectCallBack.onSelect(
                        it1
                    )
                }
            }

            changeButton.setOnClickListener {
                currencies?.currencyId?.let { it1 ->
                    onItemSelectedForChangeCallBack.onSelect(
                        it1
                    )
                }
            }

            cancelButton.setOnClickListener {
                dialog?.cancel()
            }

            builder.setView(layout)
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }
}