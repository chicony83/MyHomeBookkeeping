package com.chico.myhomebookkeeping.ui.categories

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.EditNameTextWatcher
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.showKeyboard

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CategoryDao

    private var selectedCategoryId = 0

    private val uiHelper = UiHelper()
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private lateinit var uiControl: UiControl
    private val showHideDialogsController = ShowHideDialogsController()
    private val uiColors = UiColors()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).categoryDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)

        with(categoriesViewModel) {
            selectedCategory.observe(viewLifecycleOwner, {
                binding.confirmationLayout.selectedItemName.text = it?.categoryName
            })

            categoriesList.observe(viewLifecycleOwner, {
                binding.categoryHolder.adapter =
                    CategoriesAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            uiControl.showSelectLayoutHolder()
                            categoriesViewModel.loadSelectedCategory(selectedId)
                            selectedCategoryId = selectedId
                        }
                    })
            })
            changeCategory.observe(viewLifecycleOwner, {
                binding.changeCategoryLayout.categoryName.setText(it?.categoryName)
                if (it?.isIncome == true) {
                    uiHelper.setTrueOnRadioButton(binding.changeCategoryLayout.incomingRadioButton)
                }
                if (it?.isIncome == false) {
                    uiHelper.setTrueOnRadioButton(binding.changeCategoryLayout.spendingRadioButton)
                }
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiControl = UiControl(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCategoryFragmentButton,
            newItemLayoutHolder = binding.newCategoryLayoutHolder,
            confirmationLayoutHolder = binding.confirmationLayoutHolder,
            changeItemLayoutHolder = binding.changeCategoryLayoutHolder
        )

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        navControlHelper = NavControlHelper(control)

        if (navControlHelper.isPreviousFragment(R.id.nav_money_moving_query)) {
            with(uiHelper) {
                hideUiElement(binding.showHideAddCategoryFragmentButton)
                showUiElement(binding.selectAllButton)
                showUiElement(binding.allIncomeSpendingLayout)
            }
        } else if (navControlHelper.isPreviousFragment(R.id.nav_money_moving)) {
            with(uiHelper) {
                showUiElement(binding.selectAllButton)
                showUiElement(binding.allIncomeSpendingLayout)
            }
        }
        view.hideKeyboard()
        with(binding) {
            selectAllIncomeButton.setOnClickListener {
                categoriesViewModel.selectIncomeCategory(navControlHelper)
                navControlHelper.moveToMoneyMovingFragment()
            }
            selectAllSpendingButton.setOnClickListener {
                categoriesViewModel.selectSpendingCategory(navControlHelper)
                navControlHelper.moveToMoneyMovingFragment()
            }
            selectAllButton.setOnClickListener {
                categoriesViewModel.selectAllCategories(navControlHelper)
                navControlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCategoryFragmentButton.setOnClickListener {
                uiControl.showNewItemLayoutHolder()
                view.showKeyboard()

                val result: Any = categoriesViewModel.getNamesList()

                if (result is List<*>) {
                    val namesList: List<String> = result as List<String>
                    binding.newCategoryLayout.categoryName.addTextChangedListener(
                        EditNameTextWatcher(
                            namesList,
                            binding.newCategoryLayout.addNewCategoryButton,
                            binding.newCategoryLayout.errorThisNameIsTaken
                        )
                    )
                }

                with(binding.newCategoryLayout.categoryName) {
                    requestFocus()
                    setSelection(0)
                }
            }
            with(newCategoryLayout) {
                addNewCategoryButton.setOnClickListener {
                    if (uiHelper.isVisibleLayout(binding.newCategoryLayoutHolder)) {
                        if (uiHelper.isLengthStringMoThan(binding.newCategoryLayout.categoryName.text)) {
                            if ((uiHelper.isCheckedRadioButton(binding.newCategoryLayout.incomingRadioButton)
                                        or
                                        uiHelper.isCheckedRadioButton(binding.newCategoryLayout.spendingRadioButton)
                                        )
                            ) {
                                val category =
                                    binding.newCategoryLayout.categoryName.text.toString()
                                val isIncoming: Boolean = isSelectedCategoryIncome(
                                    binding.newCategoryLayout.incomingRadioButton,
//                                    binding.newCategoryLayout.spendingRadioButton
                                )
                                val newCategory = Categories(
                                    categoryName = category,
                                    isIncome = isIncoming
                                )
                                categoriesViewModel.addNewCategory(newCategory)
                                clearingUiElements()
                                uiHelper.hideUiElement(binding.newCategoryLayoutHolder)
                                view.hideKeyboard()
                                showUIControlElements()
                            } else {
                                showMessage(getString(R.string.message_select_type_of_category))
                            }
                        } else showMessage(getString(R.string.message_too_short_name))
                    }
                }
                cancelCreateButton.setOnClickListener {
                    clearingUiElements()
                    uiHelper.hideUiElement(binding.newCategoryLayoutHolder)
                    view.hideKeyboard()
                    showUIControlElements()
                }
            }
            with(confirmationLayout) {
                selectButton.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        categoriesViewModel.selectIdCategory(navControlHelper)
                        navControlHelper.moveToPreviousPage()
                    }
                }
                selectedItemName.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCategoryLayout.categoryName) {
                            requestFocus()
                            setSelection(0)
                        }
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCategoryLayout.categoryName) {
                            requestFocus()
                            setSelection(0)
                        }
                    }
                }
                cancelButton.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        selectedCategoryId = 0
                        uiHelper.hideUiElement(binding.confirmationLayoutHolder)
                    }
                }
            }
            with(changeCategoryLayout) {
                cancelChange.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        selectedCategoryId = 0
                    }
                    categoriesViewModel.resetCategoryForSelect()
                    categoriesViewModel.resetCategoryForChange()
                    uiHelper.hideUiElement(binding.changeCategoryLayoutHolder)
                    view.hideKeyboard()
                    showUIControlElements()
                }

                saveChange.setOnClickListener {
                    val name: String
                    val isIncome: Boolean
                    if (uiHelper.isLengthStringMoThan(binding.changeCategoryLayout.categoryName.text)) {
                        name = binding.changeCategoryLayout.categoryName.text.toString()

                        if (uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.incomingRadioButton) or
                            uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.spendingRadioButton)
                        ) {
                            isIncome = isSelectedCategoryIncome(
                                binding.changeCategoryLayout.incomingRadioButton,
//                                binding.changeCategoryLayout.spendingRadioButton
                            )
                            launchIo {
                                categoriesViewModel.saveChangedCategory(
                                    name = name,
                                    isIncome = isIncome
                                )
                            }
                            uiHelper.hideUiElement(binding.changeCategoryLayoutHolder)
                            view.hideKeyboard()
                            showUIControlElements()
                        } else {
                            showMessage(getString(R.string.message_select_category))
                        }
                    } else {
                        showMessage(getString(R.string.message_too_short_name))
                    }
                }
            }
        }
        checkUiMode()
    }

    private fun showUIControlElements() {
        showHideDialogsController.showUIControlElements(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCategoryFragmentButton
        )
    }

    private fun putItemForChange() {
        uiControl.showChangeLayoutHolder()
        categoriesViewModel.selectToChange()
        selectedCategoryId = 0
    }

    private fun clearingUiElements() {
        uiHelper.clearUiListRadioButton(
            listOf(
                binding.newCategoryLayout.incomingRadioButton,
                binding.newCategoryLayout.spendingRadioButton
            )
        )
        uiHelper.clearUiElement(binding.newCategoryLayout.categoryName)
    }

    @SuppressLint("ResourceAsColor")
    private fun checkUiMode() {
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                with(uiColors) {
                    setDialogBackgroundColor(getDialogsList(), R.drawable.dialog_background_night)
                    setButtonsBackgroundColor(getButtonsListForColorButton(),getNightColorForButtonsBackground())
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                with(uiColors) {
                    setDialogBackgroundColor(getDialogsList(), R.drawable.dialog_background_day)
                    setButtonsBackgroundColor(getButtonsListForColorButton(),getDayColorForButtonsBackground())
                    setColorTextOnButton(getButtonsListForColorButtonText(), getDayColorForButtonsText())
                }
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                with(uiColors) {
                    setDialogBackgroundColor(getDialogsList(), R.drawable.dialog_background_day)
                    setButtonsBackgroundColor(getButtonsListForColorButton(),getDayColorForButtonsBackground())
                    setColorTextOnButton(getButtonsListForColorButtonText(), getDayColorForButtonsText())
                }
            }
        }
    }

    private fun getDayColorForButtonsText() = resources.getColor(R.color.colorPrimaryVariant)

    private fun getButtonsListForColorButtonText() = listOf(
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton
    )

    private fun getDayColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonDayBackground)
    }

    private fun getNightColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonNightBackground)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun getButtonsBackgroundColor(color: Int): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColorStateList(color, null)
        } else {
            resources.getColorStateList(color)
        }
    }

    private fun getButtonsListForColorButton() = listOf(
        binding.newCategoryLayout.addNewCategoryButton,
        binding.newCategoryLayout.cancelCreateButton,
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton,
        binding.confirmationLayout.cancelButton,
        binding.changeCategoryLayout.saveChange,
        binding.changeCategoryLayout.cancelChange
    )

    private fun getDialogsList() = listOf(
        binding.newCategoryLayout,
        binding.changeCategoryLayout,
        binding.confirmationLayout
    )

    private fun isSelectedCategoryIncome(
        incomingRadioButton: RadioButton,
//        spendingRadioButton: RadioButton
    ): Boolean {
        return incomingRadioButton.isChecked
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}