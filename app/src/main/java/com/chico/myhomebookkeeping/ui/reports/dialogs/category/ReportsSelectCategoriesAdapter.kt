package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack

class ReportsSelectCategoriesAdapter(
    private val categoriesList: List<Categories>,
    private val onItemCheckedCallBack: OnItemCheckedCallBack
) :
    RecyclerView.Adapter<ReportsSelectCategoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_categories_for_reports, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoriesList[position])
    }

    override fun getItemCount() = categoriesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemIdTextView: TextView? = null
        var isCheckedCheckBox: CheckBox? = null
        var nameTextView: TextView? = null
        var amountTextView: TextView? = null

        init {
            itemIdTextView = itemView.findViewById(R.id.itemId)
            isCheckedCheckBox = itemView.findViewById(R.id.isChecked)
            nameTextView = itemView.findViewById(R.id.name)
            amountTextView = itemView.findViewById(R.id.amount)
        }

        fun bind(categories: Categories) {

            if (categories.isIncome){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.setBackgroundColor(
                        itemView.resources.getColor(R.color.incomeBackgroundColor,null)
                    )
                }
            }
            if (!categories.isIncome){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.setBackgroundColor(
                        itemView.resources.getColor(R.color.spendingBackgroundColor,null)
                    )
                }
            }

            isCheckedCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
                run {
                    if (isChecked) categories.categoriesId?.let {
                        onItemCheckedCallBack.onChecked(it)
                    }
                    if (!isChecked) categories.categoriesId?.let {
                        onItemCheckedCallBack.onUnChecked(it)
                    }
                }
            }
            nameTextView?.text = categories.categoryName

        }

    }
}