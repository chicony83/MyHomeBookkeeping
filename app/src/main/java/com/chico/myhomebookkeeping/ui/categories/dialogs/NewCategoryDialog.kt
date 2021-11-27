package com.chico.myhomebookkeeping.ui.categories.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.categories.OnAddNewCategoryCallBack
import com.chico.myhomebookkeeping.utils.getString
import java.lang.IllegalStateException

class NewCategoryDialog(
    private val result: Any,
    private val onAddNewCategoryCallBack: OnAddNewCategoryCallBack
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_category, null)

            val editText = layout.findViewById<EditText>(R.id.category_name)
            val errorTextView = layout.findViewById<TextView>(R.id.error_this_name_is_taken)
            val incomeRadioButton = layout.findViewById<RadioButton>(R.id.incoming_radio_button)
            val spendingRadioButton = layout.findViewById<RadioButton>(R.id.spending_radio_button)

            val addButton = layout.findViewById<Button>(R.id.addNewCategoryButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)

            addButton.setOnClickListener {
                if (editText.text.isNotEmpty()) {
                    val name = editText.getString()
                    if (incomeRadioButton.isChecked
                        or spendingRadioButton.isChecked) {
                            showMessage("название категории $name")
                        if (incomeRadioButton.isChecked){
                         showMessage("категория доходов")
                        }
                        else if (spendingRadioButton.isChecked){
                            showMessage("категория расходов")
                        }
                    }
                    else if (!incomeRadioButton.isChecked
                        or !spendingRadioButton.isChecked) {
                        showMessage(getString(R.string.message_select_type_of_category))
                    }
                }
                else if (editText.text.isEmpty()) {
                    showMessage(getString(R.string.message_too_short_name))
                }
//                val isIncome = getIsIncome()
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

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

}