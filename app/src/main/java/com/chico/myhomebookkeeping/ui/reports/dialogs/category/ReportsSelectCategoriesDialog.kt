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
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack
import com.chico.myhomebookkeeping.interfaces.reports.dialogs.OnSelectedCategoriesCallBack
import java.lang.IllegalStateException

class ReportsSelectCategoriesDialog(
    private val categoriesList: List<Categories>,
    private val onSelectedCategoriesCallBack: OnSelectedCategoriesCallBack
) : DialogFragment() {

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

            val reportsCategoriesViewModel = ReportsSelectCategoriesViewModel()

            recyclerView.setItemViewCacheSize(categoriesList.size)

            recyclerView.adapter = ReportsSelectCategoriesAdapter(categoriesList,object :OnItemCheckedCallBack{
                override fun onChecked(id: Int) {
                    Message.log("checked id = $id")
                    reportsCategoriesViewModel.addCategoryInSetOfCategories(id)
                }

                override fun onUnChecked(id: Int) {
                    Message.log("unchecked id = $id")
                    reportsCategoriesViewModel.deleteCategoryInSetOfCategories(id)
                }

            })

            submitButton.setOnClickListener {
                onSelectedCategoriesCallBack.select(reportsCategoriesViewModel.getCategoriesSet())
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