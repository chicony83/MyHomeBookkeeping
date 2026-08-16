package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesForReportsBinding
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack
import com.google.android.material.checkbox.MaterialCheckBox

class ReportsSelectCategoriesAdapter(
    private val list: List<ReportsCategoriesItem>,
    private val onItemCheckedCallBack: OnItemCheckedCallBack
) :
    RecyclerView.Adapter<ReportsSelectCategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportsSelectCategoriesAdapter.ViewHolder {
        val binding = RecyclerViewItemCategoriesForReportsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReportsSelectCategoriesAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemCategoriesForReportsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportsCategoriesItem) {
            with(binding) {
                itemId.text = item.id.toString()
                categoryNameTextView.text = item.name
                iconImg.setImageResource(item.icon ?: R.drawable.no_image)
                isCheckedCheckBox.setOnCheckedChangeListener(null)
                isCheckedCheckBox.checkedState = when {
                    item.isPartiallyChecked -> MaterialCheckBox.STATE_INDETERMINATE
                    item.isChecked -> MaterialCheckBox.STATE_CHECKED
                    else -> MaterialCheckBox.STATE_UNCHECKED
                }
                // Keep the partial state visible on themes that do not draw Material's indeterminate icon.
                indeterminateMark.visibility = if (item.isPartiallyChecked) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                isCheckedCheckBox.setOnClickListener {
                    if (isCheckedCheckBox.checkedState == MaterialCheckBox.STATE_CHECKED) {
                        setCheckOnItem(item.id)
                        Message.log("selected item ${item.id}")
                    } else {
                        setUnCheckOnItem(item.id)
                        Message.log("unselected item ${item.id}")
                    }
                }
                itemView.setOnClickListener {
                    if (item.isChecked) {
                        setUnCheckOnItem(item.id)
                    } else {
                        setCheckOnItem(item.id)
                    }
                }
            }
        }
    }

    private fun setUnCheckOnItem(id: Int) {
        onItemCheckedCallBack.onUnChecked(id)
    }

    private fun setCheckOnItem(id: Int) {
        onItemCheckedCallBack.onChecked(id)
    }
}
