package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.view.LayoutInflater
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

                isCheckedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    run {
                        if (isChecked) item.id.let {
                            setCheckOnItem(it)
                            Message.log("selected item $it")
                        }
                        if (!isChecked) item.id.let {
                            setUnCheckOnItem(it)
                            Message.log("unselected item $it")
                        }
                    }
                }
                itemView.setOnClickListener {
                    run {
                        if (isCheckedCheckBox.checkedState == MaterialCheckBox.STATE_CHECKED) item.id.let {
                            isCheckedCheckBox.checkedState = MaterialCheckBox.STATE_UNCHECKED
                        } else {
                            item.id.let {
                                isCheckedCheckBox.checkedState = MaterialCheckBox.STATE_CHECKED
                            }
                        }
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
