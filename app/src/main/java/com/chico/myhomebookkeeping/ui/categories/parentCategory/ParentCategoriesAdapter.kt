package com.chico.myhomebookkeeping.ui.categories.parentCategory

import android.app.Application
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemParentCategoriesBinding
import com.chico.myhomebookkeeping.db.entity.ParentCategories
import com.chico.myhomebookkeeping.interfaces.OnClickCreateNewElementCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import kotlin.coroutines.coroutineContext

class ParentCategoriesAdapter(
    parentCategoriesList: List<ParentCategories>,
    val onItemViewClickListener: OnItemViewClickListener,
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

                parentCategoryNameTextView.text = parentCategory.name

                parentCategoriesItem.setOnClickListener {
                    val id = idParentCategories.text.toString().toInt()

                    onItemViewClickListener.onLongClick(id)
                    onItemViewClickListener.onShortClick(id)
                }
            }
        }

        fun bindNoParentCategory() {
            with(binding) {
                noParentCategoryItem.visibility = View.VISIBLE
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