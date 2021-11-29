package com.chico.myhomebookkeeping.ui.categories.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBack
import java.lang.IllegalStateException

class SelectCategoryDialog(
    val categories: Categories?,
    private val onItemSelectForChangeCallBack: OnItemSelectForChangeCallBack,
    private val onItemSelectForSelectCallBack: OnItemSelectForSelectCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_category, null)

            val name = layout.findViewById<TextView>(R.id.selectedItemName)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            categories?.let { it1 -> name.text = it1.categoryName }

            name.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                closeDialog()
            }
            selectButton.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForSelectCallBack.onSelect(it1)
                }
                closeDialog()
            }
            changeButton.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                closeDialog()
            }
            cancelButton.setOnClickListener {
                closeDialog()
            }

            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun closeDialog() {
        dialog?.cancel()
    }
}