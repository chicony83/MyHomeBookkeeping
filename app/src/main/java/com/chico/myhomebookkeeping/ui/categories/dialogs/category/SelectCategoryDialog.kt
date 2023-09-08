package com.chico.myhomebookkeeping.ui.categories.dialogs.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import kotlinx.android.synthetic.main.dialog_select_category.view.parent_category_name_TextView
import java.lang.IllegalStateException

class SelectCategoryDialog(
    private val categories: Categories?,
    private val onItemSelectForChangeCallBack: OnItemSelectForChangeCallBack,
    private val onItemSelectForSelectCallBackInt: OnItemSelectForSelectCallBackInt
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_category, null)

            val parentCategoryName = layout.parent_category_name_TextView

            val iconImg = layout.findViewById<ImageView>(R.id.iconImg)
            val name = layout.findViewById<TextView>(R.id.selectedItemName)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            categories?.let { it1 ->
                parentCategoryName.text = it1.parentCategoryId.toString()
                name.text = it1.categoryName
                iconImg.setImageResource(it1.icon ?: R.drawable.no_image)
            }

            name.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                dialogCancel()
            }
            selectButton.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForSelectCallBackInt.onSelect(it1)
                }
                dialogCancel()
            }
            changeButton.setOnClickListener {
                categories?.categoriesId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
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