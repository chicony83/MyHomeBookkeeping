package com.chico.myhomebookkeeping.ui.currencies.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyCallBack
import java.lang.IllegalStateException

class ChangeCurrencyDialog(
    val currency: Currencies?,
    private val onChangeCurrencyCallBack: OnChangeCurrencyCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_currency, null)

            val nameEditText = layout.findViewById<EditText>(R.id.currencyNameEditText)
            val nameShortEditTex = layout.findViewById<EditText>(R.id.currencyNameShortEditText)
            val iSOEditText = layout.findViewById<EditText>(R.id.currencyISOEditText)

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            nameEditText.setText(currency?.currencyName.toString())

            if (!currency?.currencyNameShort.isNullOrEmpty()){
                nameShortEditTex.setText(currency?.currencyNameShort.toString())
            }

            if (!currency?.iso4217.isNullOrEmpty()){
                iSOEditText.setText(currency?.iso4217.toString())
            }

            saveButton.setOnClickListener {
                if (nameEditText.text.isNotEmpty()){
                    val name = nameEditText.text.toString()
                    val nameShort:String? = getText(nameShortEditTex)
                    val iso:String? = getText(iSOEditText)
                    val isLengthChecked:Boolean = CheckString.isLengthMoThan(name)
                    if (isLengthChecked){
                        onChangeCurrencyCallBack.change(
                            id = currency?.currencyId ?: 0,
                            name = name,
                            nameShort = nameShort,
                            iSO = iso

                        )
                        dialogCancel()
                    }else if (!isLengthChecked){
                        showMessage(getString(R.string.message_too_short_name))
                    }
                }else if(nameEditText.text.isEmpty()){
                    showMessage(getString(R.string.message_too_short_name))
                }

            }

            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.setView(layout)
            builder.create()
        } ?: throw  IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun getText(getText: EditText?): String? {
        return if (!getText?.text.isNullOrEmpty()){
            Toast.makeText(requireContext(), "${getText?.text}",Toast.LENGTH_LONG).show()
            getText?.text.toString()
        } else null
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }
}