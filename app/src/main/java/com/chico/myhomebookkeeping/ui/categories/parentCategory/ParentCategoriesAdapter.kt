package com.chico.myhomebookkeeping.ui.categories.parentCategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemParentCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.interfaces.OnClickCreateNewElementCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.interfaces.categories.OnSelectNoCategories

class ParentCategoriesAdapter(
    parentCategoriesList: List<ParentCategories>,
    val onItemViewClickListener: OnItemViewClickListener,
    val onSelectNoCategories: OnSelectNoCategories,
    val clickCreateNewCurrencyListener: OnClickCreateNewElementCallBack,

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

    override fun getItemCount(): Int = initList.size + 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        com.chico.myhomebookkeeping.helpers.Message.log("list size ${initList.size.toString()}")
        if (position < initList.size) {
            holder.bind(initList[position])
        }else{
            if (position == initList.size + 1) {
                holder.bindNoParentCategory()
            }
            if (position == initList.size + 2) {
                holder.bindAddNewParentCategory()
            }
        }
    }

    inner class ViewHolder(
        private val binding: RecyclerViewItemParentCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parentCategory: ParentCategories) {
            with(binding) {
                parentCategoriesItem.visibility = View.VISIBLE

                idParentCategories.text = parentCategory.id.toString()
                parentCategoryNameTextView.text = parentCategory.name

                parentCategoriesItem.setOnLongClickListener{
                    parentCategory.id?.let {
                        it1->onItemViewClickListener.onLongClick(it1)
                    }
                    true
                }
                parentCategoriesItem.setOnClickListener {
                    parentCategory.id?.let {
                        it1->onItemViewClickListener.onShortClick(it1)
                    }
                }
            }
        }

        fun bindNoParentCategory() {
            with(binding) {
                with(noParentCategoryItem){
                    visibility = View.VISIBLE
                    setOnClickListener {
                        onSelectNoCategories.onSelect()
                    }
                }
            }
        }

        fun bindAddNewParentCategory() {
            with(binding) {
                newParentCategoriesItem.visibility = View.VISIBLE

                addNewParentCategoryImageView.setOnClickListener {
                    clickCreateNewCurrencyListener.onPress()
                }
            }
        }
    }
}