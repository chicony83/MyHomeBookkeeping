package com.chico.myhomebookkeeping.ui.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.Categories

class CategoriesAdapter(
    categoriesList: List<Categories>,
    val listener: OnItemViewClickListener
) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var initList = categoriesList

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(categoriesList: List<Categories>) {
        initList = categoriesList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewItemCategoriesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(initList[position])
    }

    override fun getItemCount() = initList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Categories) {
            with(binding) {
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
                if (category.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        categoriesItem.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.incomeBackgroundColor,
                                null
                            )
                        )
                    }
                }
                if (!category.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        categoriesItem.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.spendingBackgroundColor,
                                null
                            )
                        )
                    }
                }
            }
        }
    }
}