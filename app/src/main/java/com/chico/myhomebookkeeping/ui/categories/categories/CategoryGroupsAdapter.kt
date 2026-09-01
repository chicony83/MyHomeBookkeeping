package com.chico.myhomebookkeeping.ui.categories.categories

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoryTileBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoryGroupBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemParentCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.helpers.displayName
import com.chico.myhomebookkeeping.icons.setCategoryIcon
import com.chico.myhomebookkeeping.interfaces.OnClickCreateNewElementCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants

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
    private val onFavoriteClick: (Categories) -> Unit,
    private val createNewParentCategoryListener: OnClickCreateNewElementCallBack,
    private val onTopOrderChanged: (List<String>, List<ParentCategories>) -> Unit,
    private val onCategoriesOrderChanged: (List<Categories>) -> Unit,
    private val displayMode: String,
    private var showAddRows: Boolean = true,
    private var showUsageCount: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var groups = groups
    private var topOrder = normalizeTopOrder(topOrder, groups)
    private val expandedGroupIds = mutableSetOf<Int?>()
    private var rows = buildRows()
    private var editMode = false
    private var dragStartListener: ((RecyclerView.ViewHolder) -> Unit)? = null
    private var draggingTopKey: String? = null
    private var pendingTopOrderChanged = false
    private var pendingCategoriesOrderChanged = false
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        groups: List<CategoryGroup>,
        topOrder: List<String>,
        expandAll: Boolean,
        showAddRows: Boolean,
        showUsageCount: Boolean
    ) {
        this.groups = groups
        this.topOrder = normalizeTopOrder(topOrder, groups)
        this.showAddRows = showAddRows
        this.showUsageCount = showUsageCount
        if (expandAll) {
            expandedGroupIds.clear()
            expandedGroupIds.addAll(groups.map { it.parentCategory?.id })
        }
        rows = buildRows()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun collapseAll() {
        expandedGroupIds.clear()
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
        draggingTopKey?.let { return moveTopRow(it, toRow) }
        return when {
            fromRow.isTopRow() -> moveTopRow(fromRow.topKey() ?: return false, toRow)
            fromRow is CategoryTreeRow.CategoryItem -> moveCategory(
                fromRow,
                toRow,
                fromPosition,
                movingDown = fromPosition < toPosition
            )
            else -> false
        }
    }

    fun commitPendingOrderChanges() {
        draggingTopKey = null
        if (pendingTopOrderChanged) {
            onTopOrderChanged(topOrder, orderedParentCategories())
            pendingTopOrderChanged = false
        }
        if (pendingCategoriesOrderChanged) {
            onCategoriesOrderChanged(groups.flatMap { it.categories })
            pendingCategoriesOrderChanged = false
        }
    }

    private fun moveTopRow(fromKey: String, toRow: CategoryTreeRow): Boolean {
        val toKey = toRow.targetTopKey() ?: return false
        val fromPosition = positionOfTopKey(fromKey)
        val newTopOrder = moveTopKey(topOrder, fromKey, toKey)
        if (newTopOrder == topOrder) return false
        topOrder = newTopOrder
        rows = buildRows()
        val newPosition = positionOfTopKey(fromKey)
        if (fromPosition == RecyclerView.NO_POSITION || newPosition == RecyclerView.NO_POSITION) {
            notifyDataSetChanged()
        } else {
            notifyItemMoved(fromPosition, newPosition)
        }
        pendingTopOrderChanged = true
        return true
    }

    private fun moveCategory(
        fromRow: CategoryTreeRow.CategoryItem,
        toRow: CategoryTreeRow,
        fromPosition: Int,
        movingDown: Boolean
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
            is CategoryTreeRow.AddCategory -> toRow.parentCategory?.id
            else -> return false
        }

        categoriesByParent[fromParentId]?.removeAll { it.categoriesId == fromCategory.categoriesId }
        val targetList = categoriesByParent.getOrPut(targetParentId) { mutableListOf() }
        val insertIndex = when (toRow) {
            is CategoryTreeRow.CategoryItem -> {
                val targetIndex = targetList.indexOfFirst {
                    it.categoriesId == toRow.category.categoriesId
                }.takeIf { it >= 0 } ?: targetList.size
                if (movingDown) targetIndex + 1 else targetIndex
            }
            is CategoryTreeRow.AddCategory -> targetList.size
            else -> 0
        }.coerceIn(0, targetList.size)
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
        val newPosition = positionOfCategory(fromCategory)
        if (newPosition == RecyclerView.NO_POSITION) {
            notifyDataSetChanged()
        } else {
            notifyItemMoved(fromPosition, newPosition)
        }
        pendingCategoriesOrderChanged = true
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY, VIEW_TYPE_ADD_CATEGORY -> CategoryViewHolder(
                RecyclerViewItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TYPE_CATEGORY_TILE, VIEW_TYPE_ADD_CATEGORY_TILE -> CategoryTileViewHolder(
                RecyclerViewItemCategoryTileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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
            is CategoryTreeRow.CategoryItem -> if (isGridMode()) {
                VIEW_TYPE_CATEGORY_TILE
            } else {
                VIEW_TYPE_CATEGORY
            }
            is CategoryTreeRow.AddCategory -> if (isGridMode()) {
                VIEW_TYPE_ADD_CATEGORY_TILE
            } else {
                VIEW_TYPE_ADD_CATEGORY
            }
            is CategoryTreeRow.AddParent -> VIEW_TYPE_ADD_PARENT
            else -> VIEW_TYPE_HEADER
        }
    }

    fun getSpanSize(position: Int): Int {
        return if (
            isGridMode() &&
            (rows[position] is CategoryTreeRow.CategoryItem || rows[position] is CategoryTreeRow.AddCategory)
        ) {
            1
        } else {
            GRID_SPAN_COUNT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is CategoryTreeRow.ParentHeader -> (holder as HeaderViewHolder).bind(row.group)
            is CategoryTreeRow.NoParentHeader -> (holder as HeaderViewHolder).bind(row.group)
            is CategoryTreeRow.CategoryItem -> if (holder is CategoryTileViewHolder) {
                holder.bind(row.category)
            } else {
                (holder as CategoryViewHolder).bind(row.category, isLastCategoryInGroup(position))
            }
            is CategoryTreeRow.AddCategory -> if (holder is CategoryTileViewHolder) {
                holder.bindAddCategory(row.parentCategory)
            } else {
                (holder as CategoryViewHolder).bindAddCategory(row.parentCategory)
            }
            CategoryTreeRow.AddParent -> (holder as AddParentCategoryViewHolder).bind()
        }
    }

    private fun isLastCategoryInGroup(position: Int): Boolean {
        val nextRow = rows.getOrNull(position + 1)
        return nextRow == null || nextRow.isTopRow()
    }

    inner class HeaderViewHolder(
        private val binding: RecyclerViewItemCategoryGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(group: CategoryGroup) {
            val parentCategory = group.parentCategory
            val isExpanded = expandedGroupIds.contains(parentCategory?.id)
            val languageTag = AppLanguage.getSelectedTag(itemView.context)
            val title = parentCategory?.displayName(languageTag)
                ?: itemView.context.getString(R.string.text_on_button_no_parent_category)
            with(binding) {
                groupCategoriesRecyclerView.visibility = View.GONE
                groupNameTextView.text = title
                groupIconImageView.setCategoryIcon(parentCategory?.iconKey)
                groupExpandImageView.setImageResource(
                    if (isExpanded) R.drawable.ic_expand_remove
                    else R.drawable.ic_expand_add
                )
                groupDragHandleImageView.visibility = if (editMode) View.VISIBLE else View.GONE
                root.cardElevation = if (isExpanded) 0f else itemView.resources.displayMetrics.density
                root.radius = if (isExpanded) 0f else 12f * itemView.resources.displayMetrics.density
                root.translationZ = 0f
                categoryGroupHeader.setBackgroundResource(
                    if (isExpanded) R.drawable.category_group_header_expanded_background
                    else R.drawable.category_group_background
                )
                groupHeaderDivider.visibility =
                    if (isExpanded && !isGridMode()) View.VISIBLE else View.GONE
                root.setBottomMargin(
                    if (isExpanded) 0
                    else itemView.resources.getDimensionPixelSize(R.dimen.margin_half_normal)
                )
                groupDragHandleImageView.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        draggingTopKey = group.topKey
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
        fun bind(category: Categories, isLastInGroup: Boolean) {
            val languageTag = AppLanguage.getSelectedTag(itemView.context)
            with(binding) {
                addNewCategoryItem.visibility = View.GONE
                categoriesItem.visibility = View.VISIBLE
                categoryItemCardView.setBottomMargin(
                    if (isLastInGroup) itemView.resources.getDimensionPixelSize(R.dimen.margin_half_normal)
                    else 0
                )
                categoriesItem.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = itemView.dpToPx(52)
                }
                categoriesItem.setPadding(
                    categoriesItem.paddingLeft,
                    0,
                    categoriesItem.paddingRight,
                    categoriesItem.paddingBottom
                )
                categoriesItem.setBackgroundResource(
                    if (isLastInGroup) R.drawable.category_child_row_bottom_background
                    else R.drawable.category_child_row_background
                )
                categoryItemDivider.visibility = if (isLastInGroup) View.GONE else View.VISIBLE
                categoryDragHandleImageView.visibility = if (editMode) View.VISIBLE else View.GONE
                iconImg.setCategoryIcon(category.iconKey)
                idCategories.text = category.categoriesId.toString()
                categoryNameTextView.text = category.displayName(languageTag)
                categoryFavoriteImageView.visibility = if (editMode) View.GONE else View.VISIBLE
                categoryUsageCountTextView.visibility =
                    if (!editMode && showUsageCount) View.VISIBLE else View.GONE
                categoryUsageCountTextView.text = category.usageCount.toString()
                categoryFavoriteImageView.setImageResource(
                    if (category.isFavorite) R.drawable.ic_star_favorite_full
                    else R.drawable.ic_star_favorite_outline
                )
                categoryFavoriteImageView.setOnClickListener {
                    onFavoriteClick(category)
                }
                categoriesItem.setOnClickListener {
                    category.categoriesId?.let { categoryListener.onShortClick(it) }
                }
                categoriesItem.setOnLongClickListener {
                    category.categoriesId?.let { categoryListener.onLongClick(it) }
                    true
                }
                categoryDragHandleImageView.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        draggingTopKey = null
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
                categoryItemCardView.setBottomMargin(
                    itemView.resources.getDimensionPixelSize(R.dimen.margin_half_normal)
                )
                categoryItemDivider.visibility = View.GONE
                categoryFavoriteImageView.setOnClickListener(null)
                addNewCategoryTextView.text = itemView.context.getString(R.string.text_on_button_add_new_subcategory)
                addNewCategoryItem.setOnClickListener {
                    onPressCreateNewCategory(parentCategory)
                }
                addNewCategoryImageView.setOnClickListener {
                    onPressCreateNewCategory(parentCategory)
                }
            }
        }
    }

    inner class CategoryTileViewHolder(
        private val binding: RecyclerViewItemCategoryTileBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Categories) {
            val languageTag = AppLanguage.getSelectedTag(itemView.context)
            with(binding) {
                categoryIconImageView.setCategoryIcon(category.iconKey)
                categoryIconImageView.contentDescription = itemView.context.getString(
                    R.string.content_description_icon_item_of_category
                )
                categoryNameTextView.text = category.displayName(languageTag)
                categoryFavoriteImageView.visibility = View.VISIBLE
                categoryFavoriteImageView.setImageResource(
                    if (category.isFavorite) R.drawable.ic_star_favorite_full
                    else R.drawable.ic_star_favorite_outline
                )
                categoryFavoriteImageView.setOnClickListener {
                    onFavoriteClick(category)
                }
                categoryUsageCountTextView.visibility =
                    if (showUsageCount) View.VISIBLE else View.GONE
                categoryUsageCountTextView.text = category.usageCount.toString()
                val operationColor = ContextCompat.getColor(
                    itemView.context,
                    if (category.isIncome) {
                        R.color.categoryIncomeIndicator
                    } else {
                        R.color.categorySpendingIndicator
                    }
                )
                categoryTypeIndicator.visibility = View.VISIBLE
                categoryTypeIndicator.backgroundTintList = ColorStateList.valueOf(operationColor)
                categoryTileCardView.strokeColor = ColorUtils.setAlphaComponent(
                    ContextCompat.getColor(itemView.context, R.color.categoryDivider),
                    TILE_OUTLINE_ALPHA
                )
                categoryTileCardView.setOnClickListener {
                    category.categoriesId?.let { categoryListener.onShortClick(it) }
                }
                categoryTileCardView.setOnLongClickListener {
                    category.categoriesId?.let { categoryListener.onLongClick(it) }
                    true
                }
            }
        }

        fun bindAddCategory(parentCategory: ParentCategories?) {
            with(binding) {
                categoryFavoriteImageView.visibility = View.GONE
                categoryFavoriteImageView.setOnClickListener(null)
                categoryUsageCountTextView.visibility = View.GONE
                categoryTypeIndicator.visibility = View.GONE
                categoryIconImageView.setImageResource(R.drawable.ic_add_circle)
                categoryIconImageView.contentDescription = itemView.context.getString(
                    R.string.content_description_icon_add_new_element
                )
                categoryNameTextView.text = itemView.context.getString(
                    R.string.text_on_button_add_new_subcategory
                )
                categoryTileCardView.strokeColor = ColorUtils.setAlphaComponent(
                    ContextCompat.getColor(itemView.context, R.color.categoryDivider),
                    TILE_OUTLINE_ALPHA
                )
                categoryTileCardView.setOnLongClickListener(null)
                categoryTileCardView.setOnClickListener {
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
                addNewParentCategoryTextView.text = itemView.context.getString(R.string.text_on_button_add_new_category)
                newParentCategoriesItem.setOnClickListener {
                    createNewParentCategoryListener.onPress()
                }
                addNewParentCategoryImageView.setOnClickListener {
                    createNewParentCategoryListener.onPress()
                }
                newParentCategoriesItem.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        draggingTopKey = TOP_ADD_PARENT
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
                TOP_ADD_PARENT -> if (showAddRows) listOf(CategoryTreeRow.AddParent) else emptyList()
                else -> {
                    val group = groupsByKey[key] ?: return@flatMap emptyList()
                    val header = if (group.parentCategory == null) {
                        CategoryTreeRow.NoParentHeader(group)
                    } else {
                        CategoryTreeRow.ParentHeader(group)
                    }
                    if (expandedGroupIds.contains(group.parentCategory?.id)) {
                        val categoryRows = group.categories.map {
                            CategoryTreeRow.CategoryItem(it, group.parentCategory?.id)
                        }
                        if (showAddRows) {
                            listOf(header) + categoryRows + CategoryTreeRow.AddCategory(group.parentCategory)
                        } else {
                            listOf(header) + categoryRows
                        }
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

    private fun positionOfCategory(category: Categories): Int {
        return rows.indexOfFirst {
            it is CategoryTreeRow.CategoryItem && it.category.categoriesId == category.categoriesId
        }.takeIf { it >= 0 } ?: RecyclerView.NO_POSITION
    }

    private fun positionOfTopKey(topKey: String): Int {
        return rows.indexOfFirst { it.topKey() == topKey }
            .takeIf { it >= 0 } ?: RecyclerView.NO_POSITION
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

    private fun CategoryTreeRow.targetTopKey(): String? = when (this) {
        is CategoryTreeRow.ParentHeader -> group.topKey
        is CategoryTreeRow.NoParentHeader -> TOP_WITHOUT_PARENT
        is CategoryTreeRow.CategoryItem -> parentCategoryId?.let { TOP_PARENT_PREFIX + it } ?: TOP_WITHOUT_PARENT
        is CategoryTreeRow.AddCategory -> parentCategory?.id?.let { TOP_PARENT_PREFIX + it } ?: TOP_WITHOUT_PARENT
        CategoryTreeRow.AddParent -> TOP_ADD_PARENT
    }

    private fun isGridMode(): Boolean = displayMode == Constants.CATEGORIES_DISPLAY_MODE_GRID

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_CATEGORY = 1
        const val VIEW_TYPE_ADD_CATEGORY = 2
        const val VIEW_TYPE_ADD_PARENT = 3
        const val VIEW_TYPE_CATEGORY_TILE = 4
        const val VIEW_TYPE_ADD_CATEGORY_TILE = 5
        const val GRID_SPAN_COUNT = 3
        const val TILE_OUTLINE_ALPHA = 104
    }
}

private fun View.setBottomMargin(bottomMargin: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.bottomMargin = bottomMargin
    }
}

private fun View.dpToPx(value: Int): Int = (resources.displayMetrics.density * value + 0.5f).toInt()

const val TOP_PARENT_PREFIX = "parent:"
const val TOP_WITHOUT_PARENT = "without_parent"
const val TOP_ADD_PARENT = "add_parent"

fun normalizeTopOrder(savedOrder: List<String>, groups: List<CategoryGroup>): List<String> {
    val actualKeys = groups.map { it.topKey } + TOP_ADD_PARENT
    return (savedOrder.filter { it in actualKeys } + actualKeys).distinct()
}

fun moveTopKey(topOrder: List<String>, fromKey: String, toKey: String): List<String> {
    val fromIndex = topOrder.indexOf(fromKey)
    val toIndex = topOrder.indexOf(toKey)
    if (fromIndex == -1 || toIndex == -1 || fromIndex == toIndex) return topOrder
    return topOrder.toMutableList().apply {
        removeAt(fromIndex)
        add(toIndex, fromKey)
    }
}
