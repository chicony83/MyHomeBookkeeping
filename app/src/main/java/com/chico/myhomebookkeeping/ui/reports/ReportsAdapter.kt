package com.chico.myhomebookkeeping.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemReportsBinding

class ReportsAdapter(
    private val itemsList: List<ReportsItem>
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
    }

    override fun getItemCount() =  itemsList.size

    class ViewHolder(
        private val binding: RecyclerViewItemReportsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reportsItem: ReportsItem) {
            with(binding){
                itemId.text = reportsItem.id.toString()
                name.text = reportsItem.name
            }
        }

    }

}