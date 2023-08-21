package com.chico.myhomebookkeeping.ui.categories.parentCategory

import android.view.LayoutInflater
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

    override fun getItemCount(): Int  = initList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(initList[position])
    }

    inner class ViewHolder(
        private val binding: RecyclerViewItemParentCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parentCategory: ParentCategories) {
            with(binding){
                parentCategoryNameTextView.text = parentCategory.name
            }
        }
    }
}