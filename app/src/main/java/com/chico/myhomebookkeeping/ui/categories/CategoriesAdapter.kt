package com.chico.myhomebookkeeping.ui.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.entity.ParentCategory
import com.chico.myhomebookkeeping.domain.entities.NormalizedCategory
import com.chico.myhomebookkeeping.enums.fromNameResToParentCategoriesEnum
import com.chico.myhomebookkeeping.interfaces.OnCategoryClickListener

class CategoriesAdapter(
    categoriesList: List<NormalizedCategory>,
    val listener: OnCategoryClickListener
) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var initList = categoriesList

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(categoriesList: List<NormalizedCategory>) {
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
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: NormalizedCategory) {
            val ctx = binding.root.context
            with(binding) {
                if (category.nameRes!=null) root.contentDescription = ctx.getString(category.nameRes)
                else root.contentDescription = category.name

                idCategories.text = category.categoriesId.toString()

                iconImg.setImageResource(category.iconRes ?: R.drawable.no_image)

                if (category.nameRes!=null) categoryCardViewText.text = ctx.getString(category.nameRes)
                else categoryCardViewText.text = category.name

                categoriesItem.setOnLongClickListener {
                    category.let { it1 -> listener.onLongClick(it1) }
                    true
                }
                categoriesItem.setOnClickListener {
                    category.let { it1 -> listener.onShortClick(it1) }
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