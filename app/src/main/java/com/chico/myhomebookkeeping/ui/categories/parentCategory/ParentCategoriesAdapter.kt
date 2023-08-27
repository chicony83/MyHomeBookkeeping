package com.chico.myhomebookkeeping.ui.categories.parentCategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemParentCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener

class ParentCategoriesAdapter(
    parentCategoriesList: List<ParentCategories>,
    val listener: OnItemViewClickListener
) : RecyclerView.Adapter<ParentCategoriesAdapter.ViewHolder>() {

    private var initList = parentCategoriesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemParentCategoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = initList.size + 2

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < initList.size) {
            holder.bind(initList[position])
        } else if (position == initList.size + 1) {
            holder.bindNoParentCategory()
        } else if (position == initList.size + 2) {
            holder.bindAddNewPArentCategory()
        }
    }

    inner class ViewHolder(
        private val binding: RecyclerViewItemParentCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parentCategory: ParentCategories) {
            with(binding) {
                parentCategoryNameTextView.text = parentCategory.name
            }
        }

        fun bindNoParentCategory() {
            with(binding){
                parentCategoriesItem.visibility == View.GONE
                noParentCategoryItem.visibility==View.VISIBLE
            }
        }

        fun bindAddNewPArentCategory() {
            with(binding){
                parentCategoriesItem.visibility == View.GONE
                newParentCategoriesItem.visibility == View.VISIBLE
            }
        }
    }
}