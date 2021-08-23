package com.chico.myhomebookkeeping.ui.moneyMoving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemMoneyMovingBinding
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis

class MoneyMovingAdapter(
    private val moneyMovementList: List<FullMoneyMoving>,
    val listener: OnItemViewClickListenerLong
) : RecyclerView.Adapter<MoneyMovingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemMoneyMovingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
       return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moneyMovementList[position])
    }

    override fun getItemCount() = moneyMovementList.size

    inner class ViewHolder(private val binding: RecyclerViewItemMoneyMovingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(moneyMovement: FullMoneyMoving) {
            with(binding){
                dataTime.text = moneyMovement.timeStamp.parseTimeFromMillis()
                amount.text = moneyMovement.amount.toString()
                cashAccountName.text = moneyMovement.cashAccountNameValue
                currencyName.text = moneyMovement.currencyNameValue
                categoryName.text = moneyMovement.categoryNameValue
                if (!moneyMovement.description.isNullOrEmpty()){
                    description.text = moneyMovement.description
                    description.visibility = View.VISIBLE
                }

                if (moneyMovement.isIncome){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.amount.setTextColor(itemView.resources.getColor(R.color.incomeTextColor,null))
                    }
                }
                if (!moneyMovement.isIncome){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.amount.setTextColor(itemView.resources.getColor(R.color.spendingTextColor,null))
                    }
                }

                moneyMovingItem.setOnClickListener {
                    moneyMovement.id.let { listener.onClick(it) }
                }
            }
        }
    }
}