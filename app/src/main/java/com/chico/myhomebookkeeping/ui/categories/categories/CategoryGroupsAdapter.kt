package com.chico.myhomebookkeeping.ui.categories.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoryGroupBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemParentCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.interfaces.OnClickCreateNewElementCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener

data class CategoryGroup(
    val parentCategory: ParentCategories?,
    val categories: List<Categories>,
    val topKey: String = parentCategory?.id?.let { TOP_PARENT_PREFIX + it } ?: TOP_WITHOUT_PARENT
)

sealed class CategoryTreeRow {
    data class ParentHeader(val group: CategoryGroup) : CategoryTreeRow()
    data class NoParentHeader(val group: CategoryGroup) : CategoryTreeRow()
    data class CategoryItem(val category: Categories, val parentCategoryId: Int?) : CategoryTreeRow()
    data class AddCategory(val parentCategory: ParentCategories?) : CategoryTreeRow()
    object AddParent : CategoryTreeRow()
}

class CategoryGroupsAdapter(
    groups: List<CategoryGroup>,
    topOrder: List<String>,
    private val categoryListener: OnItemViewClickListener,
    private val onPressCreateNewCategory: (ParentCategories?) -> Unit,
    private val createNewParentCategoryListener: OnClickCreateNewElementCallBack,
    private val onTopOrderChanged: (List<String>, List<ParentCategories>) -> Unit,
    private val onCategoriesOrderChanged: (List<Categories>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var groups = groups
    private var topOrder = normalizeTopOrder(topOrder, groups)
    private val expandedGroupIds = mutableSetOf<Int?>()
    private var rows = buildRows()
    private var editMode = false
    private var dragStartListener: ((RecyclerView.ViewHolder) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(groups: List<CategoryGroup>, topOrder: List<String>, expandAll: Boolean) {
        this.groups = groups
        this.topOrder = normalizeTopOrder(topOrder, groups)
        if (expandAll) {
            expandedGroupIds.clear()
            expandedGroupIds.addAll(groups.map { it.parentCategory?.id })
        }
        rows = buildRows()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEditMode(enabled: Boolean) {
        editMode = enabled
        notifyDataSetChanged()
    }

    fun setDragStartListener(listener: (RecyclerView.ViewHolder) -> Unit) {
        dragStartListener = listener
    }

    fun moveItem(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition !in rows.indices || toPosition !in rows.indices) return false
        val fromRow = rows[fromPosition]
        val toRow = rows[toPosition]
        return when {
            fromRow.isTopRow() && toRow.isTopRow() -> moveTopRow(fromRow, toRow)
            fromRow is CategoryTreeRow.CategoryItem -> moveCategory(fromRow, toRow)
            else -> false
        }
    }

    private fun moveTopRow(fromRow: CategoryTreeRow, toRow: CategoryTreeRow): Boolean {
        val fromKey = fromRow.topKey() ?: return false
        val toKey = toRow.topKey() ?: return false
        val mutableTopOrder = topOrder.toMutableList()
        val fromIndex = mutableTopOrder.indexOf(fromKey)
        val toIndex = mutableTopOrder.indexOf(toKey)
        if (fromIndex == -1 || toIndex == -1) return false
        mutableTopOrder.removeAt(fromIndex)
        mutableTopOrder.add(toIndex, fromKey)
        topOrder = mutableTopOrder
        rows = buildRows()
        notifyDataSetChanged()
        onTopOrderChanged(topOrder, orderedParentCategories())
        return true
    }

    private fun moveCategory(
        fromRow: CategoryTreeRow.CategoryItem,
        toRow: CategoryTreeRow
    ): Boolean {
        val categoriesByParent = groups.associate { group ->
            group.parentCategory?.id to group.categories.toMutableList()
        }.toMutableMap()
        val fromCategory = fromRow.category
        val fromParentId = fromRow.parentCategoryId
        val targetParentId = when (toRow) {
            is CategoryTreeRow.CategoryItem -> toRow.parentCategoryId
            is CategoryTreeRow.ParentHeader -> toRow.group.parentCategory?.id
            is CategoryTreeRow.NoParentHeader -> null
            else -> return false
        }

        categoriesByParent[fromParentId]?.removeAll { it.categoriesId == fromCategory.categoriesId }
        val targetList = categoriesByParent.getOrPut(targetParentId) { mutableListOf() }
        val insertIndex = when (toRow) {
            is CategoryTreeRow.CategoryItem -> targetList.indexOfFirst {
                it.categoriesId == toRow.category.categoriesId
            }.takeIf { it >= 0 } ?: targetList.size
            else -> 0
        }
        targetList.add(
            insertIndex,
            fromCategory.copy(parentCategoryId = targetParentId).apply {
                categoriesId = fromCategory.categoriesId
            }
        )

        groups = groups.map { group ->
            group.copy(categories = categoriesByParent[group.parentCategory?.id].orEmpty())
        }
        rows = buildRows()
        notifyDataSetChanged()
        onCategoriesOrderChanged(groups.flatMap { it.categories })
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY, VIEW_TYPE_ADD_CATEGORY -> CategoryViewHolder(
                RecyclerViewItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TYPE_ADD_PARENT -> AddParentCategoryViewHolder(
                RecyclerViewItemParentCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> HeaderViewHolder(
                RecyclerViewItemCategoryGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemCount(): Int = rows.size

    override fun getItemViewType(position: Int): Int {
        return when (rows[position]) {
            is CategoryTreeRow.CategoryItem -> VIEW_TYPE_CATEGORY
            is CategoryTreeRow.AddCategory -> VIEW_TYPE_ADD_CATEGORY
            is CategoryTreeRow.AddParent -> VIEW_TYPE_ADD_PARENT
            else -> VIEW_TYPE_HEADER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is CategoryTreeRow.ParentHeader -> (holder as HeaderViewHolder).bind(row.group)
            is CategoryTreeRow.NoParentHeader -> (holder as HeaderViewHolder).bind(row.group)
            is CategoryTreeRow.CategoryItem -> (holder as CategoryViewHolder).bind(row.category)
            is CategoryTreeRow.AddCategory -> (holder as CategoryViewHolder).bindAddCategory(row.parentCategory)
            CategoryTreeRow.AddParent -> (holder as AddParentCategoryViewHolder).bind()
        }
    }

    inner class HeaderViewHolder(
        private val binding: RecyclerViewItemCategoryGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(group: CategoryGroup) {
            val parentCategory = group.parentCategory
            val isExpanded = expandedGroupIds.contains(parentCategory?.id)
            val title = parentCategory?.name
                ?: itemView.context.getString(R.string.text_on_button_no_parent_category)
            with(binding) {
                groupCategoriesRecyclerView.visibility = View.GONE
                groupNameTextView.text = title
                groupCountTextView.text = group.categories.size.toString()
                groupIconImageView.setImageResource(parentCategory?.icon ?: R.drawable.no_image)
                groupExpandImageView.setImageResource(
                    if (isExpanded) R.drawable.category_arrow_drop_up
                    else R.drawable.category_arrow_drop_down
                )
                groupDragHandleImageView.visibility = if (editMode) View.VISIBLE else View.GONE
                groupDragHandleImageView.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        dragStartListener?.invoke(this@HeaderViewHolder)
                    }
                    false
                }
                categoryGroupHeader.setOnClickListener {
                    if (expandedGroupIds.contains(parentCategory?.id)) {
                        expandedGroupIds.remove(parentCategory?.id)
                    } else {
                        expandedGroupIds.add(parentCategory?.id)
                    }
                    rows = buildRows()
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class CategoryViewHolder(
        private val binding: RecyclerViewItemCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(category: Categories) {
            with(binding) {
                addNewCategoryItem.visibility = View.GONE
                categoriesItem.visibility = View.VISIBLE
                categoryDragHandleImageView.visibility = if (editMode) View.VISIBLE else View.GONE
                iconImg.setImageResource(category.icon ?: R.drawable.no_image)
                idCategories.text = category.categoriesId.toString()
                categoryNameTextView.text = category.categoryName
                categoriesItem.setOnClickListener {
                    category.categoriesId?.let { categoryListener.onShortClick(it) }
                }
                categoriesItem.setOnLongClickListener {
                    category.categoriesId?.let { categoryListener.onLongClick(it) }
                    true
                }
                categoryDragHandleImageView.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        dragStartListener?.invoke(this@CategoryViewHolder)
                    }
                    false
                }
                val indicatorColor = if (category.isIncome) {
                    R.color.categoryIncomeIndicator
                } else {
                    R.color.categorySpendingIndicator
                }
                categoryTypeIndicator.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, indicatorColor)
                )
            }
        }

        fun bindAddCategory(parentCategory: ParentCategories?) {
            with(binding) {
                categoriesItem.visibility = View.GONE
                addNewCategoryItem.visibility = View.VISIBLE
                addNewCategoryImageView.setOnClickListener {
                    onPressCreateNewCategory(parentCategory)
                }
            }
        }
    }

    inner class AddParentCategoryViewHolder(
        private val binding: RecyclerViewItemParentCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind() {
            with(binding) {
                allCategoriesItem.visibility = View.GONE
                parentCategoriesItem.visibility = View.GONE
                noParentCategoryItem.visibility = View.GONE
                newParentCategoriesItem.visibility = View.VISIBLE
                addNewParentCategoryImageView.setOnClickListener {
                    createNewParentCategoryListener.onPress()
                }
                newParentCategoriesItem.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        dragStartListener?.invoke(this@AddParentCategoryViewHolder)
                    }
                    false
                }
            }
        }
    }

    private fun buildRows(): List<CategoryTreeRow> {
        val groupsByKey = groups.associateBy { it.topKey }
        return topOrder.flatMap { key ->
            when (key) {
                TOP_ADD_PARENT -> listOf(CategoryTreeRow.AddParent)
                else -> {
                    val group = groupsByKey[key] ?: return@flatMap emptyList()
                    val header = if (group.parentCategory == null) {
                        CategoryTreeRow.NoParentHeader(group)
                    } else {
                        CategoryTreeRow.ParentHeader(group)
                    }
                    if (expandedGroupIds.contains(group.parentCategory?.id)) {
                        listOf(header) + group.categories.map {
                            CategoryTreeRow.CategoryItem(it, group.parentCategory?.id)
                        } + CategoryTreeRow.AddCategory(group.parentCategory)
                    } else {
                        listOf(header)
                    }
                }
            }
        }
    }

    private fun orderedParentCategories(): List<ParentCategories> {
        val groupsByKey = groups.associateBy { it.topKey }
        return topOrder.mapNotNull { groupsByKey[it]?.parentCategory }
    }

    private fun CategoryTreeRow.isTopRow(): Boolean = this is CategoryTreeRow.ParentHeader ||
        this is CategoryTreeRow.NoParentHeader ||
        this is CategoryTreeRow.AddParent

    private fun CategoryTreeRow.topKey(): String? = when (this) {
        is CategoryTreeRow.ParentHeader -> group.topKey
        is CategoryTreeRow.NoParentHeader -> TOP_WITHOUT_PARENT
        CategoryTreeRow.AddParent -> TOP_ADD_PARENT
        else -> null
    }

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_CATEGORY = 1
        const val VIEW_TYPE_ADD_CATEGORY = 2
        const val VIEW_TYPE_ADD_PARENT = 3
    }
}

const val TOP_PARENT_PREFIX = "parent:"
const val TOP_WITHOUT_PARENT = "without_parent"
const val TOP_ADD_PARENT = "add_parent"

fun normalizeTopOrder(savedOrder: List<String>, groups: List<CategoryGroup>): List<String> {
    val actualKeys = groups.map { it.topKey } + TOP_ADD_PARENT
    return (savedOrder.filter { it in actualKeys } + actualKeys).distinct()
}
