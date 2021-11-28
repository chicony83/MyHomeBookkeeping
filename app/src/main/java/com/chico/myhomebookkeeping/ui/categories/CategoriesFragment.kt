package com.chico.myhomebookkeeping.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.enums.SortingCategories
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.interfaces.categories.OnAddNewCategoryCallBack
import com.chico.myhomebookkeeping.ui.categories.dialogs.NewCategoryDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.showKeyboard

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CategoryDao

    private var selectedCategoryId: Int = 0

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
            sortedByTextOnButton.observe(viewLifecycleOwner,{
                binding.sortingCategoriesButton.text = it
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
            sortingCategoriesButton.setOnClickListener {
                Message.log("click popup menu button")
                val popupMenu = PopupMenu(context, sortingCategoriesButton)
                popupMenu.menuInflater.inflate(R.menu.pop_up_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.sort_by_numbers_ASC -> {
                            Message.log("sort by numbers ASC")
                            categoriesViewModel.setSortingCategories(SortingCategories.NumbersByASC.toString())
                            categoriesViewModel.reloadCategories()
                        }
                        R.id.sort_by_numbers_DESC -> {
                            categoriesViewModel.setSortingCategories(SortingCategories.NumbersByDESC.toString())
                            categoriesViewModel.reloadCategories()
                            Message.log("Sort by numbers DESC")
                        }
                        R.id.sort_by_alphabet_ASC -> {
                            categoriesViewModel.setSortingCategories(SortingCategories.AlphabetByASC.toString())
                            categoriesViewModel.reloadCategories()
                            Message.log("Sorting by alphabet ASC")
                        }
                        R.id.sorting_by_alphabet_DESC -> {
                            categoriesViewModel.setSortingCategories(SortingCategories.AlphabetByDESC.toString())
                            categoriesViewModel.reloadCategories()
                            Message.log("Sorting by alphabet DESC")
                        }
                    }
                    true
                }


                popupMenu.show()
            }
            showHideAddCategoryFragmentButton.setOnClickListener {
                showNewCategoryDialog()
//                uiControl.showNewItemLayoutHolder()
//                view.showKeyboard()
//
//                val result: Any = categoriesViewModel.getNamesList()
//
//                if (result is List<*>) {
//                    val namesList: List<String> = result as List<String>
//                    binding.newCategoryLayout.categoryName.addTextChangedListener(
//                        EditNameTextWatcher(
//                            namesList,
//                            getListButtons(),
//                            binding.newCategoryLayout.errorThisNameIsTaken
//                        )
//                    )
//                }
//                with(binding.newCategoryLayout.categoryName) {
//                    requestFocus()
//                    setSelection(0)
//                }
            }
//            with(newCategoryLayout) {
//                addAndSelectNewItemButton.setOnClickListener {
//                    selectedCategoryId = addNewCategory(view)
//                    if (selectedCategoryId > 0) {
//                        Message.log("selected Category ID = $selectedCategoryId")
//                        categoriesViewModel.saveData(navControlHelper, selectedCategoryId)
//                        navControlHelper.moveToPreviousPage()
//                    }
//                }
//                addNewCategoryButton.setOnClickListener {
//                    addNewCategory(view)
//                }
//                cancelCreateButton.setOnClickListener {
//                    clearingUiElements()
//                    uiHelper.hideUiElement(binding.newCategoryLayoutHolder)
//                    view.hideKeyboard()
//                    showUIControlElements()
//                }
//            }
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

//                saveChange.setOnClickListener {
//                    val name: String
//                    val isIncome: Boolean
//                    if (uiHelper.isLengthStringMoThan(binding.changeCategoryLayout.categoryName.text)) {
//                        name = binding.changeCategoryLayout.categoryName.text.toString()
//
//                        if (uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.incomingRadioButton) or
//                            uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.spendingRadioButton)
//                        ) {
//                            isIncome = isSelectedCategoryIncome(
//                                binding.changeCategoryLayout.incomingRadioButton,
////                                binding.changeCategoryLayout.spendingRadioButton
//                            )
//                            launchIo {
//                                categoriesViewModel.saveChangedCategory(
//                                    name = name,
//                                    isIncome = isIncome
//                                )
//                            }
//                            uiHelper.hideUiElement(binding.changeCategoryLayoutHolder)
//                            view.hideKeyboard()
//                            showUIControlElements()
//                        } else {
//                            showMessage(getString(R.string.message_select_category))
//                        }
//                    } else {
//                        showMessage(getString(R.string.message_too_short_name))
//                    }
//                }
            }
        }
        uiColors.setColors(
            getDialogsList(),
            getButtonsListForColorButton(),
            getButtonsListForColorButtonText()
        )
    }

    private fun showNewCategoryDialog() {
        val result = categoriesViewModel.getNamesList()
        launchUi {
            val dialog = NewCategoryDialog(result,object :OnAddNewCategoryCallBack{
                override fun add(name: String, isIncome: Boolean) {
                    Message.log("add button pressed")
//                    Message.log("new category name = $name, is Income = $isIncome")
                    showMessage("name of category $name, \n is category income = $isIncome")
                }

                override fun addAndSelect(name: String, isIncome: Boolean) {
                    Message.log("addAndSelect button pressed")
//                    Message.log("new category name = $name, is Income = $isIncome")
                    showMessage("name of category $name, \n is category income = $isIncome")
                }

            })
            dialog.show(childFragmentManager,getString(R.string.tag_show_dialog))
        }
    }

//    private fun getListButtons() = listOf(
//        binding.newCategoryLayout.addNewCategoryButton
//    )

//    private fun addNewCategory(view: View): Int {
//        if (uiHelper.isVisibleLayout(binding.newCategoryLayoutHolder)) {
//            if (uiHelper.isLengthStringMoThan(binding.newCategoryLayout.categoryName.text)) {
//                if ((uiHelper.isCheckedRadioButton(binding.newCategoryLayout.incomingRadioButton)
//                            or
//                            uiHelper.isCheckedRadioButton(binding.newCategoryLayout.spendingRadioButton)
//                            )
//                ) {
//                    val category =
//                        binding.newCategoryLayout.categoryName.text.toString()
//                    val isIncoming: Boolean = isSelectedCategoryIncome(
//                        binding.newCategoryLayout.incomingRadioButton,
//                        //                                    binding.newCategoryLayout.spendingRadioButton
//                    )
//                    val newCategory = Categories(
//                        categoryName = category,
//                        isIncome = isIncoming
//                    )
//                    clearingUiElements()
//                    uiHelper.hideUiElement(binding.newCategoryLayoutHolder)
//                    view.hideKeyboard()
//                    showUIControlElements()
//                    return categoriesViewModel.addNewCategory(newCategory).toInt()
//                } else {
//                    showMessage(getString(R.string.message_select_type_of_category))
//                    return -1
//                }
//            } else {
//                showMessage(getString(R.string.message_too_short_name))
//                return -1
//            }
//        }
//        return -1
//    }

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

//    private fun clearingUiElements() {
//        uiHelper.clearUiListRadioButton(
//            listOf(
//                binding.newCategoryLayout.incomingRadioButton,
//                binding.newCategoryLayout.spendingRadioButton
//            )
//        )
//        uiHelper.clearUiElement(binding.newCategoryLayout.categoryName)
//    }

    private fun getButtonsListForColorButtonText() = listOf(
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton
    )

    private fun getButtonsListForColorButton() = listOf(
//        binding.newCategoryLayout.addAndSelectNewItemButton,
//        binding.newCategoryLayout.addNewCategoryButton,
//        binding.newCategoryLayout.cancelCreateButton,
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton,
        binding.confirmationLayout.cancelButton,
//        binding.changeCategoryLayout.saveChange,
        binding.changeCategoryLayout.cancelChange,
    )

    private fun getDialogsList() = listOf(
//        binding.newCategoryLayout,
        binding.changeCategoryLayout,
        binding.confirmationLayout,
    )

//    private fun isSelectedCategoryIncome(
//        incomingRadioButton: RadioButton,
////        spendingRadioButton: RadioButton
//    ): Boolean {
//        return incomingRadioButton.isChecked
//    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}