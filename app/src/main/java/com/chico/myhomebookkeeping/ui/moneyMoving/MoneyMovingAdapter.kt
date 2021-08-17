package com.chico.myhomebookkeeping.ui.moneyMoving

import android.view.LayoutInflater
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
//                cashAccountName.text = moneyMovement.cashAccount.toString()
                currencyName.text = moneyMovement.currencyNameValue
                categoryName.text = moneyMovement.categoryNameValue

                if (moneyMovement.isIncome){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        moneyMovingItem.setBackgroundColor(itemView.resources.getColor(R.color.income,null))
                    }
                }
                if (!moneyMovement.isIncome){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        moneyMovingItem.setBackgroundColor(itemView.resources.getColor(R.color.spending,null))
                    }
                }

                moneyMovingItem.setOnClickListener {
                    moneyMovement.id.let { listener.onClick(it) }
                }

//                categoryName.text = moneyMovement.category.toString()
//                amount.text = moneyMovement.amount.toString()
//                if (moneyMovement.description.isNotEmpty()){
//                    description.visibility = View.VISIBLE
//                    description.text = moneyMovement.description
//                }
            }
        }
    }
}