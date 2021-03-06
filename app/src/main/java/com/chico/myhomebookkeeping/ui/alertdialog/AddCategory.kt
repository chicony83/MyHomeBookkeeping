package com.chico.myhomebookkeeping.ui.alertdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.db.dao.IncomeDao
import com.chico.myhomebookkeeping.db.entity.Income
import com.chico.myhomebookkeeping.db.incomeCategoryDB
import com.chico.myhomebookkeeping.utils.launchIo

class AddCategoryFragment() : DialogFragment() {

    private lateinit var input: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            input = EditText(context)
            input.apply {
                maxLines = 1
                hint = "enter category"
                isSingleLine = true
            }
            builder.setTitle("ALERT")

                .setMessage("WTF")

                .setView(input)
                .setPositiveButton(
                    "save"
                ){_,_->
                    saveCategory()
                }
            builder.create()
        } ?: throw IllegalAccessException("activity can not be null")

    }

    private fun saveCategory() {
        if (input.text.isNotEmpty()){
            val text: String = input.text.toString()

            val income = Income(text)
            val db: IncomeDao = incomeCategoryDB.getCategoryDB(requireContext()).incomeDao()
            launchIo {
                db.addIncomingMoneyCategory(income)
            }
            Toast.makeText(context,"$text",Toast.LENGTH_SHORT).show()

        }
        else{
            Toast.makeText(context,"поле не заполнено",Toast.LENGTH_SHORT).show()
        }
    }


}