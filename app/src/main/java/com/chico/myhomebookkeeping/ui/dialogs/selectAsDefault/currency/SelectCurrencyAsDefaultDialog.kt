package com.chico.myhomebookkeeping.ui.dialogs.selectAsDefault.currency

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Currencies

class SelectCurrencyAsDefaultDialog(
    private var selectedCurrencies: List<Currencies>
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_currency_as_default,null)
            buildLayout(layout)
            builder.setView(layout)

            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            submitButton.setOnClickListener {
                Toast.makeText(requireContext(),"select icon",Toast.LENGTH_LONG).show()
            }

            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun buildLayout(layout: View) {
        val holder = layout.findViewById<RecyclerView>(R.id.iconsHolderLayout)
        holder.adapter = SelectCurrencyAsDefaultDialogAdapter(selectedCurrencies)
    }
}