package com.chico.myhomebookkeeping.ui.categories

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiControl
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

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
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiControl = UiControl(
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
            }
            newCategoryLayout.addNewCategoryButton.setOnClickListener {
                if (uiHelper.isVisibleLayout(binding.newCategoryLayoutHolder)) {
                    if (uiHelper.isLengthStringMoThan(binding.newCategoryLayout.categoryName.text)
                        and
                        (uiHelper.isCheckedRadioButton(binding.newCategoryLayout.incomingRadioButton)
                                or
                                uiHelper.isCheckedRadioButton(binding.newCategoryLayout.spendingRadioButton)
                                )
                    ) {
                        val category = binding.newCategoryLayout.categoryName.text.toString()
                        var isIncoming = isSelectedCategoryIncome(
                            binding.newCategoryLayout.incomingRadioButton,
                            binding.newCategoryLayout.spendingRadioButton
                        )
                        val newCategory = Categories(
                            categoryName = category,
                            isIncome = isIncoming
                        )
                        categoriesViewModel.addNewCategory(newCategory)
                        uiHelper.clearUiListRadioButton(
                            listOf(
                                binding.newCategoryLayout.incomingRadioButton,
                                binding.newCategoryLayout.spendingRadioButton
                            )
                        )
                        uiHelper.clearUiElement(binding.newCategoryLayout.categoryName)
                        uiHelper.hideUiElement(binding.newCategoryLayoutHolder)
                        view.hideKeyboard()
                    } else showMessage(getString(R.string.too_short_name_message_text))
                }
            }
            with(confirmationLayout) {
                selectButton.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        categoriesViewModel.selectIdCategory(navControlHelper)
                        navControlHelper.moveToPreviousPage()
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCategoryId > 0) {
                        uiControl.showChangeLayoutHolder()
                        categoriesViewModel.selectToChange()
                        selectedCategoryId = 0
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
                }

                saveChange.setOnClickListener {
                    var name: String
                    var isIncome: Boolean
                    if (uiHelper.isLengthStringMoThan(binding.changeCategoryLayout.categoryName.text)) {
                        name = binding.changeCategoryLayout.categoryName.text.toString()

                        if (uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.incomingRadioButton) or
                            uiHelper.isCheckedRadioButton(binding.changeCategoryLayout.spendingRadioButton)
                        ) {
                            isIncome = isSelectedCategoryIncome(
                                binding.changeCategoryLayout.incomingRadioButton,
                                binding.changeCategoryLayout.spendingRadioButton
                            )
                            categoriesViewModel.saveChangedCategory(
                                name = name,
                                isIncome = isIncome
                            )
                            view.hideKeyboard()
                            uiHelper.hideUiElement(binding.changeCategoryLayoutHolder)
                        } else {
                            showMessage(getString(R.string.select_category_message_text))
                        }
                    } else {
                        showMessage(getString(R.string.too_short_name_message_text))
                    }
                }
            }
        }
        checkUiMode()
    }

    @SuppressLint("ResourceAsColor")
    private fun checkUiMode() {
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                setBackground(R.drawable.dialog_background_night)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                setBackground(R.drawable.dialog_background_day)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                setBackground(R.drawable.dialog_background_day)
            }
        }
    }

    private fun setBackground(shape: Int) {
        with(binding) {
            newCategoryLayout.root.setBackgroundResource(shape)
            changeCategoryLayout.root.setBackgroundResource(shape)
            confirmationLayout.root.setBackgroundResource(shape)
        }
    }


    private fun isSelectedCategoryIncome(
        incomingRadioButton: RadioButton,
        spendingRadioButton: RadioButton
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