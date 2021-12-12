package com.chico.myhomebookkeeping.ui.reports.fragments.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesForReportsBinding
import com.chico.myhomebookkeeping.enums.StatesReportsCategoriesAdapter
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack

class ReportsSelectCategoriesAdapter(
    private val list: List<ReportsCategoriesItem>,
    val recyclerState: String,
    val onItemCheckedCallBack: OnItemCheckedCallBack
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
                nameTextView.text = item.name.toString()
                amount.text = "many"

                when (recyclerState) {
                    StatesReportsCategoriesAdapter.SelectNone.name -> {
                        isCheckedCheckBox.isChecked = false
                    }
                    StatesReportsCategoriesAdapter.SelectAll.name -> {
                        isCheckedCheckBox.isChecked = true
                    }
                    StatesReportsCategoriesAdapter.SelectAllIncome.name -> {
                        if (item.isIncome) {
                            isCheckedCheckBox.isChecked = true
                        }
                    }
                    StatesReportsCategoriesAdapter.SelectAllSpending.name -> {
                        if (!item.isIncome) {
                            isCheckedCheckBox.isChecked = true
                        }
                    }
                }

                if (item.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        categoriesItem.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.incomeBackgroundColor,
                                null
                            )
                        )
                    }
                }
                if (!item.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        categoriesItem.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.spendingBackgroundColor,
                                null
                            )
                        )
                    }
                }
                isCheckedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    run {
                        if (isChecked) item.id.let {
                            onItemCheckedCallBack.onChecked(it)
                        }
                        if (!isChecked) item.id.let {
                            onItemCheckedCallBack.onUnChecked(it)
                        }
                    }
                }
            }
        }
    }
}