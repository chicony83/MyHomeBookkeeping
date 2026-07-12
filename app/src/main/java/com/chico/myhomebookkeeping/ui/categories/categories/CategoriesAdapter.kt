package com.chico.myhomebookkeeping.ui.categories.categories

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.interfaces.categories.OnPressCreateNewCategory

class CategoriesAdapter(
    categoriesList: List<Categories>,
    val listener: OnItemViewClickListener,
    val onPressCreateNewCategoryListener: OnPressCreateNewCategory,
    private val editMode: Boolean = false,
    private var dragStartListener: ((RecyclerView.ViewHolder) -> Unit)? = null
) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var initList = categoriesList.toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(categoriesList: List<Categories>) {
        initList = categoriesList.toMutableList()
        this.notifyDataSetChanged()
    }

    fun moveCategory(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition !in initList.indices || toPosition !in initList.indices) return false
        val category = initList.removeAt(fromPosition)
        initList.add(toPosition, category)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun getCategories(): List<Categories> = initList.toList()

    fun updateDragStartListener(listener: (RecyclerView.ViewHolder) -> Unit) {
        dragStartListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewItemCategoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        when (position) {
            in initList.indices -> holder.bind(initList[position])
            initList.size -> holder.bindAddNewCategory()
        }


    }

    override fun getItemCount() = initList.size + 1

    inner class ViewHolder(
        private val binding: RecyclerViewItemCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun resetItemState() {
            with(binding) {
                categoriesItem.visibility = View.GONE
                addNewCategoryItem.visibility = View.GONE
                categoriesItem.setOnClickListener(null)
                categoriesItem.setOnLongClickListener(null)
                categoryDragHandleImageView.setOnTouchListener(null)
                addNewCategoryImageView.setOnClickListener(null)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(category: Categories) {
            with(binding) {
                resetItemState()
                categoriesItem.visibility = View.VISIBLE
                categoryDragHandleImageView.visibility = if (editMode) View.VISIBLE else View.GONE
                root.contentDescription = category.categoryName
                idCategories.text = category.categoriesId.toString()

                iconImg.setImageResource(category.icon ?: R.drawable.no_image)

                categoryNameTextView.text = category.categoryName
                categoriesItem.setOnLongClickListener {
                    category.categoriesId?.let { it1 -> listener.onLongClick(it1) }
                    true
                }
                categoriesItem.setOnClickListener {
                    category.categoriesId?.let { it1 -> listener.onShortClick(it1) }
                }
                categoryDragHandleImageView.setOnTouchListener { _, event ->
                    if (editMode && event.actionMasked == MotionEvent.ACTION_DOWN) {
                        dragStartListener?.invoke(this@ViewHolder)
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

        fun bindAddNewCategory() {
            with(binding) {
                resetItemState()
                addNewCategoryItem.visibility = View.VISIBLE
                categoryDragHandleImageView.visibility = View.GONE
                addNewCategoryImageView.setOnClickListener {
                    onPressCreateNewCategoryListener.onPress()
                }
            }
        }
    }
}
