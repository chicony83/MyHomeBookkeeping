package com.chico.myhomebookkeeping.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.CategoriesRecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.Categories

class CategoriesAdapter(
    private val categoriesList: List<Categories>,
    val listener: OnItemViewClickListener
) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = CategoriesRecyclerViewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(categoriesList[position])
    }

    override fun getItemCount() = categoriesList.size

    inner class ViewHolder(
        private val binding: CategoriesRecyclerViewItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categories: Categories) {
            with(binding) {
                categoryCardViewText.text = categories.categoryName
                categoriesItem.setOnClickListener {
                    categories.id?.let { it1->listener.onClick(it1) }
                }

            }
        }

    }
}