package com.chico.myhomebookkeeping.ui.categories.dialogs.parentCategory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.interfaces.parentCategories.OnAddNewParentCategoryCallBack
import com.chico.myhomebookkeeping.utils.getString
import kotlinx.android.synthetic.main.dialog_new_parent_category.view.cancel_button
import kotlinx.android.synthetic.main.dialog_new_parent_category.view.parent_category_name_editText
import kotlinx.android.synthetic.main.dialog_new_parent_category.view.submit_button

class NewParentCategoryDialog(
    private val onAddNewParentCategoryCallBack: OnAddNewParentCategoryCallBack
) : DialogFragment() {

    private lateinit var iconImg: ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_parent_category, null)

            val nameEditText = layout.parent_category_name_editText
            val submitButton = layout.submit_button
            val cancelButton = layout.cancel_button

            submitButton.setOnClickListener {
                checkAndAddParentCategory(
                    nameEditText
                )
            }

            cancelButton.setOnClickListener {
                cancelDialog()
            }


            builder.setView(layout)
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun cancelDialog() {
        dialog?.dismiss()
    }

    private fun checkAndAddParentCategory(nameEditText: EditText) {
        if (nameEditText.text.isNotEmpty()) {
            val name = nameEditText.getString()
            val isLengthChecked: Boolean = CheckString.isLengthMoThan(name)

            if (isLengthChecked) {
                onAddNewParentCategoryCallBack.add(
                    name = name,
                    icon = null
                )
                cancelDialog()
            }else{
                showMessage(getString(R.string.message_too_short_name))
            }

        }


    }

    private fun showMessage(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }
}