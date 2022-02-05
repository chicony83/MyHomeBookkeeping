package com.chico.myhomebookkeeping.ui.categories.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.interfaces.OnSelectIconCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnChangeCategoryCallBack
import com.chico.myhomebookkeeping.ui.dialogs.SelectIconDialog
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import java.lang.IllegalStateException

class ChangeCategoryDialog(
    private val category: Categories?,
    private val onChangeCategoryCallBack: OnChangeCategoryCallBack
) : DialogFragment() {

    private var iconResource:Int = 0
    private lateinit var iconImg:ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_category, null)

            val nameEditText = layout.findViewById<EditText>(R.id.nameTextView)
            val incomeRadioButton = layout.findViewById<RadioButton>(R.id.incomeRadioButton)
            val spendingRadioButton = layout.findViewById<RadioButton>(R.id.spendingRadioButton)
            iconImg = layout.findViewById<ImageView>(R.id.iconImg)

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            nameEditText.setText(category?.categoryName.toString())
            iconResource = category?.icon ?: R.drawable.no_image
            iconImg.setImageResource(iconResource)

            if (category?.isIncome == true) {
                incomeRadioButton.isChecked = true
            }
            if (category?.isIncome == false) {
                spendingRadioButton.isChecked = true
            }

            iconImg.setOnClickListener {
                showSelectIconDialog()
            }

            saveButton.setOnClickListener {
                val isTypeOfCategorySelected =
                    getIsTypeOfCategorySelected(incomeRadioButton, spendingRadioButton)
                val isIncome = getTypeOfCategory(incomeRadioButton, spendingRadioButton)
                if (nameEditText.text.isNotEmpty()) {
                    val name = nameEditText.text.toString()
                    val isLengthChecked: Boolean = CheckString.isLengthMoThan(name)
                    if (isLengthChecked) {
                        if (isTypeOfCategorySelected) {
                            onChangeCategoryCallBack.changeWithIcon(
                                id = category?.categoriesId ?: 0,
                                name = nameEditText.text.toString(),
                                isIncome = isIncome,
                                iconResource = iconResource

                            )
                        }
                        dialogCancel()
                    } else if (!isLengthChecked) {
                        showMessage(getString(R.string.message_too_short_name))
                    }
                } else if (nameEditText.text.isEmpty()) {
                    showMessage(getString(R.string.message_too_short_name))
                }
            }

            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.setView(layout)
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun showSelectIconDialog() {
        launchIo {
            val db: IconResourcesDao = dataBase.getDataBase(requireContext()).iconResourcesDao()
            val iconsList = IconResourcesUseCase.getIconsList(db)
            launchUi {
                val dialog = SelectIconDialog(iconsList, object : OnSelectIconCallBack {
                    override fun selectIcon(icon: IconsResource) {
                        iconResource = icon.iconResources
                        iconImg.setImageResource(icon.iconResources)
                    }
                })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
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

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}