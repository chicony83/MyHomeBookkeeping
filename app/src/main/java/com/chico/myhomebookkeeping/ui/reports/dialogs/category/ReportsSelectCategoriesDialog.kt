package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.enums.StatesReportsCategoriesAdapter
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack
import com.chico.myhomebookkeeping.interfaces.reports.dialogs.OnSelectedCategoriesCallBack
import java.lang.IllegalStateException

class ReportsSelectCategoriesDialog(
    private val categoriesList: List<Categories>,
    private val onSelectedCategoriesCallBack: OnSelectedCategoriesCallBack
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_category_from_reports, null)

            val recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)

            val selectNoneButton = layout.findViewById<Button>(R.id.selectNoneButton)
            val selectAllButton = layout.findViewById<Button>(R.id.selectAllButton)
            val selectAllIncomeButton = layout.findViewById<Button>(R.id.selectAllIncomeButton)
            val selectAllSpendingButton = layout.findViewById<Button>(R.id.selectAllSpendingButton)

            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)
            val submitButton = layout.findViewById<Button>(R.id.submitButton)

            val reportsCategoriesViewModel = ReportsSelectCategoriesViewModel()

            val selectNone: String = StatesReportsCategoriesAdapter.SelectNone.name
            val stateSelectAll: String = StatesReportsCategoriesAdapter.SelectAll.name
            val stateSelectAllIncome: String = StatesReportsCategoriesAdapter.SelectAllIncome.name
            val stateSelectAllSpending: String =
                StatesReportsCategoriesAdapter.SelectAllSpending.name

            var stateCategoriesAdapter = mutableMapOf<String, Boolean>(
                selectNone to false,
                stateSelectAll to false,
                stateSelectAllIncome to false,
                stateSelectAllSpending to false
            )

            recyclerView.setItemViewCacheSize(categoriesList.size)


            recyclerView.adapter = getAdapter(stateCategoriesAdapter, reportsCategoriesViewModel)

            selectNoneButton.setOnClickListener {
                resetStateCategoriesAdapter(stateCategoriesAdapter)
            }

            selectAllButton.setOnClickListener {
                resetStateCategoriesAdapter(stateCategoriesAdapter)
                stateCategoriesAdapter[stateSelectAll] = true
            }

            selectAllIncomeButton.setOnClickListener {
                resetStateCategoriesAdapter(stateCategoriesAdapter)
                stateCategoriesAdapter[stateSelectAllIncome] = true
            }

            selectAllSpendingButton.setOnClickListener {
                resetStateCategoriesAdapter(stateCategoriesAdapter)
                stateCategoriesAdapter[stateSelectAllSpending] = true
            }

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

    private fun resetStateCategoriesAdapter(stateCategoriesAdapter: MutableMap<String, Boolean>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stateCategoriesAdapter.replaceAll { _, _ -> false }
        } else {
            stateCategoriesAdapter.values.forEach {
                false
            }
        }
    }

    private fun getAdapter(
        stateCategoriesAdapter: MutableMap<String, Boolean>,
        reportsCategoriesViewModel: ReportsSelectCategoriesViewModel
    ) =
        ReportsSelectCategoriesAdapter(
            stateCategoriesAdapter,
            categoriesList,
            object : OnItemCheckedCallBack {
                override fun onChecked(id: Int) {
                    Message.log("checked id = $id")
                    reportsCategoriesViewModel.addCategoryInSetOfCategories(id)
                }

                override fun onUnChecked(id: Int) {
                    Message.log("unchecked id = $id")
                    reportsCategoriesViewModel.deleteCategoryInSetOfCategories(id)
                }

            })


    private fun dialogCancel() {
        dialog?.cancel()
    }
}