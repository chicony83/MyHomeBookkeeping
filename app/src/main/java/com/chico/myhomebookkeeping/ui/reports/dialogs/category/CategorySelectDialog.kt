package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import java.lang.IllegalStateException

class CategorySelectDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_category_from_reports, null)

            val selectAllButton = layout.findViewById<Button>(R.id.selectAllButton)
            val selectAllIncomeButton = layout.findViewById<Button>(R.id.selectAllIncomeButton)
            val selectAllSpendingButton = layout.findViewById<Button>(R.id.selectAllSpendingButton)

            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            val categoryViewModel = CategoryViewModel()

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