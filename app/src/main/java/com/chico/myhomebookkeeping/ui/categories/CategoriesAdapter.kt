package com.chico.myhomebookkeeping.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.CategoriesRecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.Categorise

class CategoriesAdapter(private val categoriesList: List<Categorise>) :
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

    class ViewHolder(
        private val binding: CategoriesRecyclerViewItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categories: Categorise) {
            with(binding) {
                incomingCategoryCardViewText.text = categories.categoryName

            }
        }

    }
}