package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.FullFastPayment
import java.lang.IllegalStateException

class SelectPaymentDialog(
    val fastPayment: FullFastPayment?
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_fast_payment, null)

            buildLayout(layout)
            builder.setView(layout)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            cancelButton.setOnClickListener { dialogCancel() }

            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun buildLayout(layout: View) {
        val name = layout.findViewById<TextView>(R.id.name_fast_payment)
        val cashAccount = layout.findViewById<TextView>(R.id.cashAccountName)
        val currency = layout.findViewById<TextView>(R.id.currencyName)
        val category = layout.findViewById<TextView>(R.id.categoryName)
        val rating = layout.findViewById<ImageView>(R.id.ratingImg)
        val amount = layout.findViewById<TextView>(R.id.amount)

    }
}