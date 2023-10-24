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
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import com.chico.myhomebookkeeping.ui.categories.such.SuchName
import kotlinx.android.synthetic.main.dialog_select_category.view.parent_category_name_TextView
import java.lang.IllegalStateException

class SelectCategoryDialog(
    private val category: Categories?,
    private val parentCategoriesList: List<ParentCategories>,
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

            category?.let { it1 ->
                parentCategoryName.text = getParentCategoriesName(category.parentCategoryId)
                name.text = it1.categoryName
                iconImg.setImageResource(it1.icon ?: R.drawable.no_image)
            }

            name.setOnClickListener {
                category?.categoriesId?.let { it1 ->
                    onItemSelectForChangeCallBack.onSelect(it1)
                }
                dialogCancel()
            }
            selectButton.setOnClickListener {
                category?.categoriesId?.let { it1 ->
                    onItemSelectForSelectCallBackInt.onSelect(it1)
                }
                dialogCancel()
            }
            changeButton.setOnClickListener {
                category?.categoriesId?.let { it1 ->
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

    private fun getParentCategoriesName(parentCategoryId: Int?): String {
        var parentCategoryName = "no parent category"

        if (category?.parentCategoryId != null) {
            if (category.parentCategoryId > 0) {
                parentCategoryName =
                    SuchName().suchParentCategoryNameById(parentCategoriesList, category)
            }
        }

//        if (parentCategoryId != null) {
//            if (parentCategoryId >= 0) {
//                parentCategoryName = parentCategoryId.toString()+ " " + parentCategoriesList[parentCategoryId-1].name
//            }
//        }
        return parentCategoryName
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }
}