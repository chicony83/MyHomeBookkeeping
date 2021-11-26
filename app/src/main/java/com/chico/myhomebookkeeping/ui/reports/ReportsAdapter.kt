package com.chico.myhomebookkeeping.ui.reports

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemReportsBinding
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.ui.reports.items.ReportsCategoriesItem

class ReportsAdapter(
    private val itemsList: List<ReportsCategoriesItem>,
    val checkedCallBackListener: OnItemCheckedCallBack
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

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView
    }

    override fun getItemCount() = itemsList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemReportsBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
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
                        if (isChecked) checkedCallBackListener.onChecked(reportsCategoriesItem.id)
                        if (!isChecked) checkedCallBackListener.onUnChecked(reportsCategoriesItem.id)
                    }

                }
                if (reportsCategoriesItem.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemView.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.incomeBackgroundColor,
                                null
                            )
                        )
                    }
                }
                if (!reportsCategoriesItem.isIncome) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemView.setBackgroundColor(
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