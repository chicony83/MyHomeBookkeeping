package com.chico.myhomebookkeeping.ui.categories.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.interfaces.categories.OnChangeCategoryCallBack
import java.lang.IllegalStateException

class ChangeCategoryDialog(
    private val category: Categories?,
    private val onChangeCategoryCallBack: OnChangeCategoryCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_category, null)

            val name = layout.findViewById<EditText>(R.id.name)
            val incomeRadioButton = layout.findViewById<RadioButton>(R.id.incomeRadioButton)
            val spendingRadioButton = layout.findViewById<RadioButton>(R.id.spendingRadioButton)

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            name.setText(category?.categoryName.toString())

            if (category?.isIncome == true){
                incomeRadioButton.isChecked = true
            }
            if (category?.isIncome == false){
                spendingRadioButton.isChecked = true
            }

            saveButton.setOnClickListener {
                val isTypeOfCategorySelected =
                    getIsTypeOfCategorySelected(incomeRadioButton, spendingRadioButton)
                val isIncome = getTypeOfCategory(incomeRadioButton, spendingRadioButton)
                if (isTypeOfCategorySelected) {
                    onChangeCategoryCallBack.change(
                        id = category?.categoriesId ?: 0,
                        name = name.text.toString(),
                        isIncome = isIncome
                    )
                }
            }

            cancelButton.setOnClickListener {
                closeDialog()
            }

            builder.setView(layout)
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun getTypeOfCategory(
        incomeRadioButton: RadioButton,
        spendingRadioButton: RadioButton
    ): Boolean {
        return incomeRadioButton.isChecked
    }

    private fun getIsTypeOfCategorySelected(
        incomeRadioButton: RadioButton,
        spendingRadioButton: RadioButton
    ): Boolean {
        return incomeRadioButton.isChecked or spendingRadioButton.isChecked
    }

    private fun closeDialog() {
        dialog?.cancel()
    }
}