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
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.chico.myhomebookkeeping.interfaces.parentCategories.OnAddNewParentCategoryCallBack
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.categories.categories.CategoryGroup
import com.chico.myhomebookkeeping.ui.categories.categories.CategoryGroupsAdapter
import com.chico.myhomebookkeeping.ui.categories.categories.CategoriesViewModel
import com.chico.myhomebookkeeping.ui.categories.categories.normalizeTopOrder
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.ChangeCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.NewCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.category.SelectCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.dialogs.parentCategory.NewParentCategoryDialog
import com.chico.myhomebookkeeping.ui.categories.parentCategory.ParentCategoriesViewModel
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import java.util.*

class CategoriesFragment : Fragment() {
    companion object {
        const val ARG_ENABLE_ORDER_EDIT_MODE = "enableCategoryOrderEditMode"
    }

    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val parentCategoriesViewModel: ParentCategoriesViewModel by viewModels()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: CategoryDao
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController

//    private val uiHelper = UiHelper()
    private var searchMode = false
    private var categoryGroupsAdapter: CategoryGroupsAdapter? = null
    private var categoryOrderEditMode = false
    private var categoryTouchHelper: ItemTouchHelper? = null
    private var currentCategoriesList: List<Categories> = emptyList()
    private var currentParentCategoriesList: List<ParentCategories> = emptyList()
    private val searchMinLength = 4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).categoryDao()
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        binding.onClear = {
            hideSearch()
        }
        binding.searchTil.setEndIconOnClickListener {
            hideSearch()
        }
        with(parentCategoriesViewModel) {
            parentCategoriesList.observe(viewLifecycleOwner) {
                currentParentCategoriesList = it
                filterLists(binding.searchTil.editText?.text?.toString().orEmpty())
            }
        }
        with(categoriesViewModel) {
//            sortedByTextOnButton.observe(viewLifecycleOwner) {
//                binding.sortingButton.text = it
//            }
            categoriesList.observe(viewLifecycleOwner) {
                currentCategoriesList = it
                filterLists(binding.searchTil.editText?.text?.toString().orEmpty())
            }
        }

        with(binding) {
            searchTil.editText?.doAfterTextChanged { char ->
                filterLists(char?.toString().orEmpty())
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControlHelper = NavControlHelper(control)

        view.hideKeyboard()
        binding.categoryOrderDoneButton.setOnClickListener {
            setCategoryOrderEditMode(false)
        }
        if (arguments?.getBoolean(ARG_ENABLE_ORDER_EDIT_MODE) == true) {
            setCategoryOrderEditMode(true)
        } else {
            updateCategoryOrderDoneButtonVisibility()
        }
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

    fun toggleSearch() {
        if (searchMode) {
            hideSearch()
        } else {
            if (categoryOrderEditMode) toggleCategoryOrderEditMode()
            searchMode = true
            binding.searchTil.visibility = View.VISIBLE
            binding.searchTil.editText?.requestFocus()
        }
    }

    fun toggleCategoryOrderEditMode() {
        if (searchMode) hideSearch()
        setCategoryOrderEditMode(!categoryOrderEditMode)
    }

    private fun setCategoryOrderEditMode(isEnabled: Boolean) {
        if (isEnabled && searchMode) hideSearch()
        categoryOrderEditMode = isEnabled
        categoryGroupsAdapter?.setEditMode(categoryOrderEditMode)
        updateCategoryOrderDoneButtonVisibility()
    }

    private fun updateCategoryOrderDoneButtonVisibility() {
        binding.categoryOrderDoneButton.visibility = if (categoryOrderEditMode) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun hideSearch() {
        requireView().hideKeyboard()
        binding.searchTil.editText?.text?.clear()
        filterLists("")
        binding.searchTil.visibility = View.GONE
        searchMode = false
    }

    private fun filterLists(query: String) {
        val normalizedQuery = query.lowercase(Locale.getDefault())
        if (normalizedQuery.length < searchMinLength) {
            updateCategoryTree(
                parentCategories = currentParentCategoriesList,
                categories = currentCategoriesList,
                expandAll = false
            )
            return
        }

        val categoryMatches = currentCategoriesList.filter { category ->
            category.categoryName.lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
        val categoryMatchParentIds = categoryMatches.mapNotNull { it.parentCategoryId }.toSet()
        val parentNameMatches = currentParentCategoriesList.filter { parentCategory ->
            parentCategory.name.lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
        val parentNameMatchIds = parentNameMatches.mapNotNull { it.id }.toSet()
        val filteredParentCategories = currentParentCategoriesList.filter { parentCategory ->
            parentCategory.id?.let {
                it in categoryMatchParentIds || it in parentNameMatchIds
            } == true
        }
        val filteredCategories = currentCategoriesList.filter { category ->
            category in categoryMatches || category.parentCategoryId?.let { it in parentNameMatchIds } == true
        }
        updateCategoryTree(
            parentCategories = filteredParentCategories,
            categories = filteredCategories,
            expandAll = true
        )
    }

    private fun updateCategoryTree(
        parentCategories: List<ParentCategories>,
        categories: List<Categories>,
        expandAll: Boolean
    ) {
        val parentCategoriesById = parentCategories.mapNotNull { parentCategory ->
            parentCategory.id?.let { it to parentCategory }
        }.toMap()

        val parentGroups = parentCategories.map { parentCategory ->
            CategoryGroup(
                parentCategory = parentCategory,
                categories = categories.filter { it.parentCategoryId == parentCategory.id }
            )
        }

        val withoutParentGroup = CategoryGroup(
            parentCategory = null,
            categories = categories.filter {
                val parentCategoryId = it.parentCategoryId
                parentCategoryId == null || parentCategoriesById[parentCategoryId] == null
            }
        )

        val groups = parentGroups + withoutParentGroup
        val topOrder = getTopOrder(groups)
        val adapter = categoryGroupsAdapter
        if (adapter == null) {
            categoryGroupsAdapter = CategoryGroupsAdapter(
                groups,
                topOrder,
                object : OnItemViewClickListener {
                    override fun onShortClick(selectedId: Int) {
                        if (categoryOrderEditMode) return
                        categoriesViewModel.saveData(navControlHelper, selectedId)
                        navControlHelper.moveToPreviousFragment()
                    }

                    override fun onLongClick(selectedId: Int) {
                        if (categoryOrderEditMode) return
                        showSelectCategoryDialog(selectedId)
                    }
                },
                { parentCategory ->
                    showNewCategoryDialog(MutableLiveData(parentCategory))
                },
                object : OnClickCreateNewElementCallBack {
                    override fun onPress() {
                        showNewParentCategoryDialog()
                    }
                },
                { newTopOrder, parentCategories ->
                    saveTopOrder(newTopOrder)
                    parentCategoriesViewModel.saveParentCategoriesOrder(parentCategories)
                },
                { categories ->
                    categoriesViewModel.saveCategoriesOrder(categories)
                }
            )
            categoryGroupsAdapter?.setEditMode(categoryOrderEditMode)
            binding.categoryTreeHolder.adapter = categoryGroupsAdapter
            setupCategoryTouchHelper()
        } else {
            adapter.updateList(groups, topOrder, expandAll)
        }
    }

    private fun setupCategoryTouchHelper() {
        if (categoryTouchHelper != null) return
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                override fun isLongPressDragEnabled(): Boolean = false

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if (!categoryOrderEditMode) return false
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    if (
                        fromPosition == RecyclerView.NO_POSITION ||
                        toPosition == RecyclerView.NO_POSITION
                    ) return false
                    return categoryGroupsAdapter?.moveItem(
                        fromPosition,
                        toPosition
                    ) == true
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    categoryGroupsAdapter?.commitPendingOrderChanges()
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
            }
        )
        categoryGroupsAdapter?.setDragStartListener { holder ->
            itemTouchHelper.startDrag(holder)
        }
        itemTouchHelper.attachToRecyclerView(binding.categoryTreeHolder)
        categoryTouchHelper = itemTouchHelper
    }

    private fun getTopOrder(groups: List<CategoryGroup>): List<String> {
        val savedOrder = requireContext()
            .getSharedPreferences(Constants.SP_NAME, android.content.Context.MODE_PRIVATE)
            .getString(Constants.CATEGORIES_TOP_ORDER, "")
            .orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
        return normalizeTopOrder(savedOrder, groups)
    }

    private fun saveTopOrder(topOrder: List<String>) {
        requireContext()
            .getSharedPreferences(Constants.SP_NAME, android.content.Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.CATEGORIES_TOP_ORDER, topOrder.joinToString(","))
            .apply()
    }

    private fun showSelectCategoryDialog(selectedId: Int) {
        launchIo {
            val category: Categories? = categoriesViewModel.getSelectedCategory(selectedId)
            val parentCategoriesList: List<ParentCategories> =
                parentCategoriesViewModel.parentCategoriesList.value!!.toList().sortedBy { it.name }
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

    override fun onResume() {
        super.onResume()
        categoriesViewModel.reloadCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
