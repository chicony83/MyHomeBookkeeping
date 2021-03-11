package com.chico.myhomebookkeeping.ui.alertdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.entity.Category
import com.chico.myhomebookkeeping.db.dataBase
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

                .setMessage("category")

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

            val income = Category(categoryName = text, isIncome = false, isSpending = false)
            val db: CategoryDao = dataBase.getDataBase(requireContext()).incomeDao()
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