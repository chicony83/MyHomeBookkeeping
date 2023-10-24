package com.chico.myhomebookkeeping.ui.categories.dialogs.category

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
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.ParentCategoryHelper
import com.chico.myhomebookkeeping.interfaces.OnSelectIconCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnChangeCategoryCallBack
import com.chico.myhomebookkeeping.interfaces.currencies.dialog.OnSelectParentCategoryCallBack
import com.chico.myhomebookkeeping.ui.categories.such.SuchName
import com.chico.myhomebookkeeping.ui.dialogs.SelectIconDialog
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_change_category.view.category_name_EditText
import kotlinx.android.synthetic.main.dialog_change_category.view.parent_categories_TextView
import java.lang.IllegalStateException

class ChangeCategoryDialog(
    private val category: Categories?,
    private val parentCategoriesList: List<ParentCategories>,
    private val onChangeCategoryCallBack: OnChangeCategoryCallBack
) : DialogFragment() {

    private var iconResource: Int = 0
    private lateinit var iconImg: ImageView

    private lateinit var selectedParentCategoryName: String
    private var selectedParentCategoryId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_change_category, null)

            val categoryNameEditText = layout.category_name_EditText

            val parentCategoryTextView = layout.parent_categories_TextView

            val incomeRadioButton = layout.findViewById<RadioButton>(R.id.incomeRadioButton)
            val spendingRadioButton = layout.findViewById<RadioButton>(R.id.spendingRadioButton)
            iconImg = layout.findViewById<ImageView>(R.id.iconImg)

            val parentCategoriesNamesArray: Array<String> = parentCategoriesList.map { it1 ->
                it1.name
            }.toTypedArray()

            val saveButton = layout.findViewById<Button>(R.id.saveButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            categoryNameEditText.setText(category?.categoryName.toString())
            iconResource = category?.icon ?: R.drawable.no_image
            iconImg.setImageResource(iconResource)

            if (category?.parentCategoryId != null) {
                if (category.parentCategoryId > 0) {

                    val parentCategoryName =
                        SuchName().suchParentCategoryNameById(parentCategoriesList, category)

                    parentCategoryTextView.text = parentCategoryName
                }
            }

            parentCategoryTextView.setOnClickListener {
                showSelectParentCategoryDialog(
                    object : OnSelectParentCategoryCallBack {
                        override fun onSelect(name: String) {
                            parentCategoryTextView.text = name
                        }
                    },
                    parentCategoriesNamesArray
                )
            }

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
                if (categoryNameEditText.text.isNotEmpty()) {

                    val name = categoryNameEditText.text.toString()
                    val isLengthChecked: Boolean = CheckString.isLengthMoThan(name)
                    val categoryId = category?.categoriesId ?: 0

                    val selectedParentCategory: Int =
                        ParentCategoryHelper.getIdSelectedParentCategory(
                            parentCategoryTextView.text.toString(),
                            parentCategoriesList
                        )

                    if (isLengthChecked) {
                        if (isTypeOfCategorySelected) {

                            val isIncome = getTypeOfCategory(incomeRadioButton, spendingRadioButton)

                            if (selectedParentCategory > -1) {
                                onChangeCategoryCallBack.changeCategoryFull(
                                    id = categoryId,
                                    name = name,
                                    isIncome = isIncome,
                                    iconResource = iconResource,
                                    parentCategoryId = selectedParentCategory
                                )
                            } else {
                                onChangeCategoryCallBack.changeCategoryWithoutParentCategory(
                                    id = categoryId,
                                    name = name,
                                    isIncome = isIncome,
                                    iconResource = iconResource
                                )
                            }

                        }
                        dialogCancel()
                    } else if (!isLengthChecked) {
                        showMessage(getString(R.string.message_too_short_name))
                    }
                } else if (categoryNameEditText.text.isEmpty()) {
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

    private fun showSelectParentCategoryDialog(
        onSelectParentCategoryCallBack: OnSelectParentCategoryCallBack,
        parentCategoriesNamesArray: Array<String>
    ) {
        selectedParentCategoryName = parentCategoriesNamesArray[selectedParentCategoryId]
        launchUi {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_title_select_parent_category)
                .setSingleChoiceItems(
                    parentCategoriesNamesArray,
                    selectedParentCategoryId
                ) { _, listener ->
                    selectedParentCategoryId = listener
                    selectedParentCategoryName = parentCategoriesNamesArray[listener]
                }
                .setPositiveButton(getString(R.string.text_on_button_submit)) { _, _ ->
                    onSelectParentCategoryCallBack.onSelect(selectedParentCategoryName)
                }
                .setNegativeButton(R.string.text_on_button_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showSelectIconDialog() {
        launchIo {
            val db: IconResourcesDao = dataBase.getDataBase(requireContext()).iconResourcesDao()
            val iconsList = IconResourcesUseCase.getIconsList(db)
            Message.log("---size icons list = ${iconsList.size}")
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