package com.chico.myhomebookkeeping.ui.reports.selectCategories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCategoriesForReportsBinding
import com.chico.myhomebookkeeping.enums.StatesReportsCategoriesAdapter
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack

class ReportsSelectCategoriesAdapter(
    private val list: List<ReportsCategoriesItem>,
    private val recyclerState: String,
    private val selectedCategoriesSet: Set<Int>,
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
                nameTextView.text = item.name
                amountEditText.text = " "

                if (selectedCategoriesSet.isNotEmpty()) {
                    if (selectedCategoriesSet.contains(item.id)) {
                        isCheckedCheckBox.isChecked = true
                        setCheckOnItem(item.id)
                    }
                }

                else if (selectedCategoriesSet.isEmpty()){
                    when (recyclerState) {
                        StatesReportsCategoriesAdapter.SelectNone.name -> {
                            isCheckedCheckBox.isChecked = false
                            setUnCheckOnItem(item.id)
                        }
                        StatesReportsCategoriesAdapter.SelectAll.name -> {
                            isCheckedCheckBox.isChecked = true
                            setCheckOnItem(item.id)
                        }
                        StatesReportsCategoriesAdapter.SelectAllIncome.name -> {
                            if (item.isIncome) {
                                isCheckedCheckBox.isChecked = true
                                setCheckOnItem(item.id)
                            }
                        }
                        StatesReportsCategoriesAdapter.SelectAllSpending.name -> {
                            if (!item.isIncome) {
                                isCheckedCheckBox.isChecked = true
                                setCheckOnItem(item.id)
                            }
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
                        if (isCheckedCheckBox.isChecked) item.id.let {
                            isCheckedCheckBox.isChecked = false
                        } else if (!isCheckedCheckBox.isChecked) item.id.let {
                            isCheckedCheckBox.isChecked = true
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