package com.chico.myhomebookkeeping.ui.categories.dialogs.category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.textWathers.EditNameTextWatcher
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.IconsResource
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.domain.IconResourcesUseCase
import com.chico.myhomebookkeeping.enums.icon.names.NoCategoryNames
import com.chico.myhomebookkeeping.helpers.CheckString
import com.chico.myhomebookkeeping.helpers.ParentCategoryHelper
import com.chico.myhomebookkeeping.interfaces.OnSelectIconCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnAddNewCategoryCallBack
import com.chico.myhomebookkeeping.interfaces.currencies.dialog.OnSelectParentCategoryCallBack
import com.chico.myhomebookkeeping.ui.dialogs.SelectIconDialog
import com.chico.myhomebookkeeping.utils.getString
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.IllegalStateException

class NewCategoryDialog(
    private val result: Any,
    private val parentCategoriesResult: MutableLiveData<ParentCategories?>,
    private val parentCategoriesList: List<ParentCategories>,
    private val onAddNewCategoryCallBack: OnAddNewCategoryCallBack,
) : DialogFragment() {
    //    private val dbIcon:IconResourcesDao = dataBase.getDataBase(requireActivity().applicationContext).iconResourcesDao()
    private lateinit var iconImg: ImageView
    private lateinit var selectedIcon: IconsResource
    private lateinit var db: IconResourcesDao
    private lateinit var selectedParentCategoryName: String

    private var selectedParentCategoryId = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            db = dataBase.getDataBase(requireContext()).iconResourcesDao()
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_new_category, null)

            var namesList = listOf<String>()

            val parentCategoriesTextView = layout.findViewById<TextView>(R.id.parent_categories_TextView)

            val categoryNameEditText = layout.findViewById<EditText>(R.id.category_name_EditText)
            val errorTextView = layout.findViewById<TextView>(R.id.errorThisNameIsTaken)
            val incomeRadioButton = layout.findViewById<RadioButton>(R.id.incoming_radio_button)
            val spendingRadioButton = layout.findViewById<RadioButton>(R.id.spending_radio_button)

            if (parentCategoriesResult.value != null) {
                parentCategoriesTextView.text = parentCategoriesResult.value!!.name
            }

            iconImg = layout.findViewById<ImageView>(R.id.iconImg)

            launchIo {
                selectedIcon = IconResourcesUseCase.getIconByName(db, NoCategoryNames.NoImage.name)
//                iconImg = ImageView()
                iconImg.setImageResource(selectedIcon.iconResources)
            }

            val addButton = layout.findViewById<Button>(R.id.addNewCategoryButton)
            val addAndSelectButton = layout.findViewById<Button>(R.id.addAndSelectNewItemButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelCreateButton)

            val parentCategoriesNamesArray: Array<String> = parentCategoriesList.map { it1 ->
                it1.name
            }.toTypedArray()

            if (result is List<*>) {
                namesList = (result as List<String>)
            }
            fun buttonsList() = listOf(
                addButton, addAndSelectButton
            )

            categoryNameEditText.addTextChangedListener(
                EditNameTextWatcher(
                    namesList = namesList,
                    buttonList = buttonsList(),
                    errorMessageTexView = errorTextView
                )
            )

            parentCategoriesTextView.setOnClickListener {
                showSelectParentCategoryDialog(
                    object : OnSelectParentCategoryCallBack {
                        override fun onSelect(name: String) {
                            parentCategoriesTextView.text = name
                        }
                    },
                    parentCategoriesNamesArray
                )
            }


            addAndSelectButton.setOnClickListener {
                checkAndAddCategory(
                    categoryNameEditText,
                    parentCategoriesTextView,
                    incomeRadioButton,
                    spendingRadioButton,
                    isSelectAfterAdd = true
                )
            }

            addButton.setOnClickListener {
                checkAndAddCategory(
                    categoryNameEditText,
                    parentCategoriesTextView,
                    incomeRadioButton,
                    spendingRadioButton,
                    isSelectAfterAdd = false
                )
            }

            iconImg.setOnClickListener { showSelectIconDialog() }

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
                .setTitle(getString(R.string.dialog_title_select_parent_category))
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
                .setNegativeButton(getString(R.string.text_on_button_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun showSelectIconDialog() {
        launchIo {
            val iconsList = IconResourcesUseCase.getIconsList(db)
            launchUi {
                val dialog = SelectIconDialog(iconsList, object : OnSelectIconCallBack {
                    override fun selectIcon(icon: IconsResource) {
//                        Message.log("selected icon Id = ${icon.id}")
                        selectedIcon = icon
                        iconImg.setImageResource(icon.iconResources)

                    }
                })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    private fun checkAndAddCategory(
        nameEditText: EditText,
        parentCategoriesTextView: TextView,
        incomeRadioButton: RadioButton,
        spendingRadioButton: RadioButton,
        isSelectAfterAdd: Boolean
    ) {
        if (nameEditText.text.isNotEmpty()) {
            val nameCategory = nameEditText.getString()
            val isLengthChecked: Boolean = CheckString.isLengthMoThan(nameCategory)
            val isTypeCategorySelected =
                isSelectTypeOfCategory(incomeRadioButton, spendingRadioButton)

            val selectedParentCategoryId: Int = ParentCategoryHelper.getIdSelectedParentCategory(
                parentCategoriesTextView.text.toString(),
                parentCategoriesList,
            )

            if (isLengthChecked) {
                if (isTypeCategorySelected) {

                    val isIncomeCategory: Boolean =
                        getTypeCategoryIsIncomeDefault(incomeRadioButton, spendingRadioButton)
                    val icon = selectedIcon.iconResources

                    if (selectedParentCategoryId > -1) {
                        onAddNewCategoryCallBack.addAndSelectFull(
                            name = nameCategory,
                            parentCategoryId = selectedParentCategoryId,
                            isIncome = isIncomeCategory,
                            icon = icon,
                            isSelect = isSelectAfterAdd
                        )
                    } else {
                        onAddNewCategoryCallBack.addAndSelectWithoutParentCategory(
                            name = nameCategory,
                            isIncome = isIncomeCategory,
                            icon = icon,
                            isSelect = isSelectAfterAdd
                        )
                    }
                    dialogCancel()
                } else if (!isTypeCategorySelected) {
                    showMessage(getString(R.string.message_select_type_of_category))
                }
            } else if (!isLengthChecked) {
                showMessage(getString(R.string.message_too_short_name))
            }
        } else if (nameEditText.text.isEmpty()) {
            showMessage(getString(R.string.message_too_short_name))
        }
    }

    private fun getTypeCategoryIsIncomeDefault(
        incomeRadioButton: RadioButton,
        spendingRadioButton: RadioButton
    ): Boolean {
        return incomeRadioButton.isChecked
//        if (incomeRadioButton.isChecked) {return true}
//        else return false
    }

//    private fun getIdSelectedParentCategoryId(name: String): Int {
//        var result = -1
//        for (i in 0..parentCategoriesList.size) {
//            if (parentCategoriesList[i].name == name) {
//                result = parentCategoriesList[i].id!!
//                break
//            }
//        }
//        return result
//    }

    private fun isSelectTypeOfCategory(
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