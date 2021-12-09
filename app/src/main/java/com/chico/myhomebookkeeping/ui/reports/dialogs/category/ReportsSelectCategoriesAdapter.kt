package com.chico.myhomebookkeeping.ui.reports.dialogs.category

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.enums.StatesReportsCategoriesAdapter
import com.chico.myhomebookkeeping.interfaces.OnItemCheckedCallBack

class ReportsSelectCategoriesAdapter(
    private val stateCategoriesAdapter: MutableMap<String, Boolean>,
    private val categoriesList: List<Categories>,
    private val onItemCheckedCallBack: OnItemCheckedCallBack,
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
        var cardViewItem:LinearLayout? = null
        init {
            itemIdTextView = itemView.findViewById(R.id.itemId)
            isCheckedCheckBox = itemView.findViewById(R.id.isCheckedCheckBox)
            nameTextView = itemView.findViewById(R.id.nameTextView)
            amountTextView = itemView.findViewById(R.id.amount)
            cardViewItem = itemView.findViewById(R.id.categoriesItem)
        }

        fun bind(categories: Categories) {
            if (stateCategoriesAdapter[StatesReportsCategoriesAdapter.SelectNone.name] == true){
                isCheckedCheckBox?.isChecked = false
            }
            if (stateCategoriesAdapter[StatesReportsCategoriesAdapter.SelectAll.name] == true){
                isCheckedCheckBox?.isChecked = true
            }
            if (stateCategoriesAdapter[StatesReportsCategoriesAdapter.SelectAllIncome.name] == true){
                if (categories.isIncome){
                    isCheckedCheckBox?.isChecked = true
                }
            }
            if (stateCategoriesAdapter[StatesReportsCategoriesAdapter.SelectAllSpending.name] == true){
                if (!categories.isIncome){
                    isCheckedCheckBox?.isChecked = true
                }
            }
//            if (categories.isIncome){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    nameTextView?.setBackgroundColor(
//                        itemView.resources.getColor(R.color.incomeBackgroundColor,null)
//                    )
//                }
//            }
//            if (!categories.isIncome){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    nameTextView?.setBackgroundColor(
//                        itemView.resources.getColor(R.color.spendingBackgroundColor,null)
//                    )
//                }
//            }

            isCheckedCheckBox?.setOnCheckedChangeListener { _, isChecked ->
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