package com.chico.myhomebookkeeping.ui.MoneyMoving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.MoneyMovingRecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis

class MoneyMovingAdapter(
    private val moneyMovementList: List<MoneyMovement>
) : RecyclerView.Adapter<MoneyMovingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MoneyMovingRecyclerViewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
       return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moneyMovementList[position])
    }

    override fun getItemCount() = moneyMovementList.size

    inner class ViewHolder(private val binding: MoneyMovingRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(moneyMovement: MoneyMovement) {
            with(binding){
                dataTime.text = moneyMovement.timeStamp.parseTimeFromMillis()
                cashAccountName.text = moneyMovement.cashAccount.toString()
                currencyName.text = moneyMovement.currency.toString()
                categoryName.text = moneyMovement.category.toString()
                amount.text = moneyMovement.amount.toString()
                if (moneyMovement.description.isNotEmpty()){
                    description.visibility = View.VISIBLE
                    description.text = moneyMovement.description
                }
            }
        }
    }
}