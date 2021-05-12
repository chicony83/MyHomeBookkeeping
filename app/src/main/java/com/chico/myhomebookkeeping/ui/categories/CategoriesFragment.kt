package com.chico.myhomebookkeeping.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.ui.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CategoryDao

    private var selectedCategoryId = 0

    private val argsName: String by lazy { Constants.FOR_SELECT_CATEGORY_KEY }
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).categoryDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)

        categoriesViewModel.categoriesList.observe(viewLifecycleOwner, {
            binding.categoryHolder.adapter =
                CategoriesAdapter(it, object : OnItemViewClickListener {
                    override fun onClick(selectedId: Int) {
                        uiHelper.showHideUIElements(selectedId, binding.layoutConfirmation)
                        selectedCategoryId = selectedId
                    }
                })
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        with(binding) {
            showHideAddCategoryFragment.setOnClickListener {
                uiHelper.setShowHideOnLayout(binding.addNewCategoryFragment)
            }
            addNewCategoryButton.setOnClickListener {
                if (uiHelper.isVisibleLayout(binding.addNewCategoryFragment)) {
                    if (uiHelper.isLengthStringMoThan(binding.categoryName.text)
                        and
                        (uiHelper.isCheckedRadioButton(binding.categoryIncomingRadioButton)
                                or
                                uiHelper.isCheckedRadioButton(binding.categorySpendingRadioButton)
                                )
                    ) {
                        val category = binding.categoryName.text.toString()
                        var isIncoming = true
                        if (binding.categorySpendingRadioButton.isChecked) isIncoming = false
                        val newCategory = Categories(
                            categoryName = category,
                            isIncome = isIncoming
                        )
                        CategoriesUseCase.addNewCategoryRunBlocking(
                            db,
                            newCategory,
                            categoriesViewModel
                        )
                        uiHelper.clearUiListRadioButton(
                            listOf(
                                binding.categoryIncomingRadioButton,
                                binding.categorySpendingRadioButton
                            )
                        )
                        uiHelper.clearUiElement(binding.categoryName)
                        uiHelper.hideUiElement(binding.addNewCategoryFragment)
                        view.hideKeyboard()
                    } else showMessage(getString(R.string.too_short_name))
                }
            }
            selectButton.setOnClickListener {
                if (selectedCategoryId > 0) {
                    val bundle = Bundle()
                    bundle.putInt(argsName, selectedCategoryId)
                    findNavController().navigate(
                        R.id.nav_new_money_moving,
                        bundle
                    )
                }
            }
            cancel.setOnClickListener {
                if (selectedCategoryId > 0) {
                    selectedCategoryId = 0
                    uiHelper.hideUiElement(binding.layoutConfirmation)
                }
            }
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}