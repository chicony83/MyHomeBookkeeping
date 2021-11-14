package com.chico.myhomebookkeeping.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.`interface`.OnItemChecked
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemReportsBinding
import com.chico.myhomebookkeeping.helpers.Message

class ReportsAdapter(
    private val itemsList: List<ReportsCategoriesItem>,
    val checkedListener: OnItemChecked
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewItemReportsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView
    }

    override fun getItemCount() = itemsList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemReportsBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reportsCategoriesItem: ReportsCategoriesItem) {
            with(binding) {
                itemId.text = reportsCategoriesItem.id.toString()
                name.text = reportsCategoriesItem.name
                Message.log("loadItem reportsItem.id = ${reportsCategoriesItem.id}, reportsItem.name = ${reportsCategoriesItem.name}, reportsItem.isChecked = ${reportsCategoriesItem.isChecked}")
                if (reportsCategoriesItem.isChecked){
                    checkbox.isChecked = true
                }
                checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    run {
                        if (isChecked) checkedListener.onChecked(reportsCategoriesItem.id)
                        if (!isChecked) checkedListener.onUnChecked(reportsCategoriesItem.id)
                    }

                }
            }
        }
    }
}