package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.helpers.Message
import java.lang.IllegalStateException

class ReportsCategorySelectDialog(private val listOfCategories: List<Categories>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_category_from_reports, null)

            val recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)

            val selectAllButton = layout.findViewById<Button>(R.id.selectAllButton)
            val selectAllIncomeButton = layout.findViewById<Button>(R.id.selectAllIncomeButton)
            val selectAllSpendingButton = layout.findViewById<Button>(R.id.selectAllSpendingButton)

            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            Message.log("--- size list of Categories items = ${listOfCategories.size}")

            recyclerView.adapter = ReportsCategoriesAdapter(listOfCategories)

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