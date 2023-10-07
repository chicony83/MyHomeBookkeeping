package com.chico.myhomebookkeeping.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentCategoriesBinding
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.interfaces.OnClickCreateNewElementCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnAddNewCategoryCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnChangeCategoryCallBack
import com.chico.myhomebookkeeping.interfaces.categories.OnPressCreateNewCategory
import com.chico.myhomebookkeeping.interfaces.categories.OnSelectAllCategories
import com.chico.myhomebookkeeping.interfaces.categories.OnSelectNoCategories
import com.chico.myhomebookkeeping.interfaces.parentCategories.OnAddNewParentCategoryCallBack
import com.chico.myhomebookkeeping.ui.categories.categories.CategoriesAdapter
import com.chico.myhomebookkeeping.ui.categories.categories.CategoriesViewModel
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.ChangeCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.NewCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.SelectCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.parentCategory.NewParentCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.parentCategory.ParentCategoriesAdapter
import com.chico.myhomebookkeeping.ui.categories.parentCategory.ParentCategoriesViewModel
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.visibleInvisible
import java.util.*

class CategoriesFragment : Fragment() {

    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val parentCategoriesViewModel: ParentCategoriesViewModel by viewModels()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: CategoryDao
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController

    //    private val uiHelper = UiHelper()
    private var skipScroll = false
    private var searchMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).categoryDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        control = activity?.findNavController(R.id.nav_host_fragment)!!
        binding.onClear = {
            requireView().hideKeyboard()
//            visibleInvisible(binding.selectAllButton, true)
            visibleInvisible(binding.searchTil, false)
            searchMode = false
            skipScroll = true
        }
        with(parentCategoriesViewModel) {
            parentCategoriesList.observe(viewLifecycleOwner) {
                binding.parentCategoryHolder.adapter = ParentCategoriesAdapter(it,
                    object : OnItemViewClickListener {
                        override fun onShortClick(id: Int) {
                            categoriesViewModel.getCategoriesWithParentId(id)
                            parentCategoriesViewModel.getSelectedParentCategory(id)
                        }

                        override fun onLongClick(id: Int) {
//                            showMessage("long click on $id")
                        }
                    },
                    object : OnSelectAllCategories {
                        override fun onSelectAll() {
                            categoriesViewModel.getAllCategories()
                            parentCategoriesViewModel.eraseSelectedParentCategory()
                        }
                    },
                    object : OnSelectNoCategories {
                        override fun onSelect() {
                            categoriesViewModel.getCategoriesWithoutParentCategory()
                            parentCategoriesViewModel.eraseSelectedParentCategory()
//                            showMessage("press no category button")
                        }
                    },
                    object : OnClickCreateNewElementCallBack {
                        override fun onPress() {
                            showNewParentCategoryDialog()
                        }
                    }
                )
            }
        }
        with(categoriesViewModel) {
//            sortedByTextOnButton.observe(viewLifecycleOwner) {
//                binding.sortingButton.text = it
//            }
            categoriesList.observe(viewLifecycleOwner) {
                binding.categoryHolder.adapter =
                    CategoriesAdapter(it,
                        object : OnItemViewClickListener {
                            override fun onShortClick(selectedId: Int) {
                                categoriesViewModel.saveData(navControlHelper, selectedId)
//                                navControlHelper.moveToPreviousFragment()
                            }

                            override fun onLongClick(selectedId: Int) {
                                showSelectCategoryDialog(selectedId)
                            }
                        },
                        object : OnPressCreateNewCategory {
                            override fun onPress() {
                                val parentCategoriesResult = parentCategoriesViewModel.selectedParentCategory
                                showNewCategoryDialog(parentCategoriesResult)
                            }
                        }
                    )
            }
        }

        binding.categoryHolder.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!skipScroll) {
                    when ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()) {
                        0 -> {
//                            visibleInvisible(binding.selectAllButton, !searchMode)
                            visibleInvisible(binding.searchTil, searchMode)
                            if (!searchMode) requireView().hideKeyboard()
                        }

                        else -> {
//                            visibleInvisible(binding.selectAllButton, false)
                            visibleInvisible(binding.searchTil, true)
                        }
                    }
                } else {
                    skipScroll = false
                    searchMode = false
                }
            }
        })

        with(binding) {
            searchTil.editText?.doAfterTextChanged { char ->
                searchMode = true
                if (char.toString().isEmpty()) {
                    (categoryHolder.adapter as CategoriesAdapter).updateList(categoriesViewModel.categoriesList.value.orEmpty())
                } else {
                    val filteredCategories =
                        categoriesViewModel.categoriesList.value.orEmpty().filter {
                            it.categoryName.lowercase(Locale.getDefault()).contains(char.toString())
                        }
                    (categoryHolder.adapter as CategoriesAdapter).updateList(filteredCategories)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControlHelper = NavControlHelper(control)

        view.hideKeyboard()
//        with(binding) {
//            selectAllIncomeButton.setOnClickListener {
//                viewModel.selectIncomeCategory(navControlHelper)
//                navControlHelper.moveToMoneyMovingFragment()
//            }
//            selectAllSpendingButton.setOnClickListener {
//                viewModel.selectSpendingCategory(navControlHelper)
//                navControlHelper.moveToMoneyMovingFragment()
//            }
//            selectAllButton.setOnClickListener {
//                viewModel.selectAllCategories(navControlHelper)
//                navControlHelper.moveToMoneyMovingFragment()
//            }
//            sortingButton.setOnClickListener {
//                val popupMenu = PopupMenu(context, sortingButton)
//                popupMenu.menuInflater.inflate(
//                    R.menu.pop_up_menu_sorting_categories,
//                    popupMenu.menu
//                )
//                popupMenu.setOnMenuItemClickListener { item ->
//                    when (item.itemId) {
//                        R.id.sort_by_numbers_ASC -> {
////                            Message.log("sort by numbers ASC")
//                            sortingCategories(SortingCategories.NumbersByASC.toString())
//                        }
//
//                        R.id.sort_by_numbers_DESC -> {
//                            sortingCategories(SortingCategories.NumbersByDESC.toString())
////                            Message.log("Sort by numbers DESC")
//                        }
//
//                        R.id.sort_by_alphabet_ASC -> {
//                            sortingCategories(SortingCategories.AlphabetByASC.toString())
////                            Message.log("Sorting by alphabet ASC")
//                        }
//
//                        R.id.sort_by_alphabet_DESC -> {
//                            sortingCategories(SortingCategories.AlphabetByDESC.toString())
////                            Message.log("Sorting by alphabet DESC")
//                        }
//                    }
//                    true
//                }
//                popupMenu.show()
//            }
//        }
//        if (navControlHelper.isPreviousFragment(R.id.nav_new_money_moving)
//            or
//            navControlHelper.isPreviousFragment(R.id.nav_change_money_moving)
//        ) {
//            with(uiHelper) {
//                hideUiElement(binding.selectAllButton)
//                hideUiElement(binding.selectAllIncomeButton)
//                hideUiElement(binding.selectAllSpendingButton)
//            }
//        }
    }

//    private fun sortingCategories(sorting: String) {
//        with(categoriesViewModel) {
//            setSortingCategories(sorting)
//            reloadCategories()
//        }
//    }

    private fun showSelectCategoryDialog(selectedId: Int) {
        launchIo {
            val category: Categories? = categoriesViewModel.getSelectedCategory(selectedId)
            val parentCategoriesList: List<ParentCategories> =
                parentCategoriesViewModel.parentCategoriesList.value!!.toList()
            launchUi {
                val dialog = SelectCategoryDialog(category, parentCategoriesList,
                    object : OnItemSelectForChangeCallBack {
                        override fun onSelect(id: Int) {
                            showChangeCategoryDialog(category)
                        }
                    },
                    object : OnItemSelectForSelectCallBackInt {
                        override fun onSelect(id: Int) {
                            categoriesViewModel.saveData(navControlHelper, id)
                            navControlHelper.moveToPreviousFragment()
                        }
                    })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    private fun showChangeCategoryDialog(category: Categories?) {
        val parentCategoriesList = parentCategoriesViewModel.getParentCategoriesList()
        launchUi {
            val dialog = ChangeCategoryDialog(category,
                parentCategoriesList,
                object : OnChangeCategoryCallBack {
//                override fun changeWithoutIcon(id: Int, name: String, isIncome: Boolean) {
//                    categoriesViewModel.saveChangedCategory(id,name,isIncome)
//                }

                    override fun changeCategoryWithoutParentCategory(
                        id: Int,
                        name: String,
                        isIncome: Boolean,
                        iconResource: Int
                    ) {
                        categoriesViewModel.saveChangedCategoryWithoutParentCategory(
                            id,
                            name,
                            isIncome,
                            iconResource
                        )
                    }

                    override fun changeCategoryFull(
                        id: Int,
                        name: String,
                        isIncome: Boolean,
                        iconResource: Int,
                        parentCategoryId: Int
                    ) {
                        categoriesViewModel.saveChangedCategoryFull(
                            id,
                            name,
                            isIncome,
                            iconResource,
                            parentCategoryId
                        )
                    }
                })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun showNewCategoryDialog(parentCategoriesResult: MutableLiveData<ParentCategories?>) {
        val result = categoriesViewModel.getNamesList()
        val parentCategoriesList = parentCategoriesViewModel.getParentCategoriesList()

        launchUi {
            val dialog = NewCategoryDialog(result,
                parentCategoriesResult,
                parentCategoriesList,
                object : OnAddNewCategoryCallBack {

                    override fun addAndSelectWithoutParentCategory(
                        name: String,
                        isIncome: Boolean,
                        isSelect: Boolean,
                        icon: Int
                    ) {
                        val category = Categories(
                            categoryName = name,
                            isIncome = isIncome,
                            icon = icon,
                            parentCategoryId = null
                        )
                        val result: Long = categoriesViewModel.addNewCategory(category)
                        if (isSelect) {
                            categoriesViewModel.saveData(navControlHelper, result.toInt())
                            navControlHelper.moveToPreviousFragment()
                        }
                    }

                    override fun addAndSelectFull(
                        name: String,
                        parentCategoryId: Int,
                        isIncome: Boolean,
                        isSelect: Boolean,
                        icon: Int
                    ) {
                        val category = Categories(
                            categoryName = name,
                            isIncome = isIncome,
                            icon = icon,
                            parentCategoryId = parentCategoryId
                        )
                        val result: Long = categoriesViewModel.addNewCategory(category)
                        if (isSelect) {
                            categoriesViewModel.saveData(navControlHelper, result.toInt())
                            navControlHelper.moveToPreviousFragment()
                        }
                    }
                })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun showNewParentCategoryDialog() {
        launchUi {
            val dialog = NewParentCategoryDialog(
                object : OnAddNewParentCategoryCallBack {
                    override fun add(name: String, icon: Int?) {
                        val parentCategory = ParentCategories(
                            name = name,
                            icon = icon
                        )
                        val result: Long =
                            parentCategoriesViewModel.addNewParentCategory(parentCategory)
                    }

                }

            )
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