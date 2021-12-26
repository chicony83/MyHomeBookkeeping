package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import java.lang.IllegalStateException

class SelectPaymentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_fast_payment, null)

            buildLayout(layout)
            builder.setView(layout)

            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun buildLayout(layout: View) {

    }
}