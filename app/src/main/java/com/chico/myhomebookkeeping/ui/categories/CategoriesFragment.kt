package com.chico.myhomebookkeeping.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.enums.SortingCategories
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnAddNewCategoryCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnChangeCategoryCallBack
import com.chico.myhomebookkeeping.ui.categories.dialogs.ChangeCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.NewCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.SelectCategoryDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CategoryDao

    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).categoryDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(categoriesViewModel) {
            sortedByTextOnButton.observe(viewLifecycleOwner, {
                binding.sortingCategoriesButton.text = it
            })
            categoriesList.observe(viewLifecycleOwner, {
                binding.categoryHolder.adapter =
                    CategoriesAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            showSelectCategoryDialog(selectedId)
                        }
                    })
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControlHelper = NavControlHelper(control)

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
            }
        }
    }

    private fun showSelectCategoryDialog(selectedId: Int) {
        launchIo {
            val category: Categories? = categoriesViewModel.loadSelectedCategory(selectedId)
            launchUi {
                val dialog = SelectCategoryDialog(category,
                    object : OnItemSelectForChangeCallBack {
                        override fun onSelect(id: Int) {
                            showChangeCategoryDialog(category)
                        }
                    },
                    object : OnItemSelectForSelectCallBack {
                        override fun onSelect(id: Int) {
                            categoriesViewModel.saveData(navControlHelper, id)
                            navControlHelper.moveToPreviousPage()
                        }
                    })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    private fun showChangeCategoryDialog(category: Categories?) {
        launchIo {
            val dialog = ChangeCategoryDialog(category, object : OnChangeCategoryCallBack {
                override fun change(id: Int, name: String, isIncome: Boolean) {
                    categoriesViewModel.saveChangedCategory(id,name,isIncome)
                }
            })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun showNewCategoryDialog() {
        val result = categoriesViewModel.getNamesList()
        launchUi {
            val dialog = NewCategoryDialog(result, object : OnAddNewCategoryCallBack {

                override fun addAndSelect(name: String, isIncome: Boolean, isSelect: Boolean) {
                    val category = Categories(
                        categoryName = name,
                        isIncome = isIncome
                    )
                    val result: Long = categoriesViewModel.addNewCategory(category)
                    if (isSelect) {
                        categoriesViewModel.saveData(navControlHelper, result.toInt())
                        navControlHelper.moveToPreviousPage()
                    }
                }
            })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
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