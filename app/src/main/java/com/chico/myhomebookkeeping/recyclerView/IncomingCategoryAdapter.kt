package com.chico.myhomebookkeeping.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.Income

class IncomingCategoryAdapter(private val incomeCategoryList: List<Income>) :
    RecyclerView.Adapter<IncomingCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
    val binding = RecyclerViewItemBinding
        .inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(incomeCategoryList[position])
//        val currentItem = incomeCategoryList[position].incomeCategory
//        holder.bind(currentItem)
    }

    override fun getItemCount() = incomeCategoryList.size

    class ViewHolder(private val binding: RecyclerViewItemBinding) :
        RecyclerView.ViewHolder( binding.root) {
        fun bind(incone: Income) {
            with(binding){
                incomingCategoryCardViewText.text = incone.incomeCategory
                
            }
//            Log.i("TAG"," text = $text")
        }

    }
}