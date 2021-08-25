package com.chico.myhomebookkeeping.ui.moneyMoving

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemMoneyMovingBinding
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import kotlinx.coroutines.withContext

class MoneyMovingAdapter(
    private val moneyMovementList: List<FullMoneyMoving>,
    val listener: OnItemViewClickListenerLong
) : RecyclerView.Adapter<MoneyMovingAdapter.ViewHolder>() {
    private lateinit var positive:String
    private lateinit var negative:String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemMoneyMovingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val context = parent.context
        positive = context.getString(R.string.positive_value)
        negative = context.getString(R.string.negative_value)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moneyMovementList[position])
    }

    override fun getItemCount() = moneyMovementList.size

    inner class ViewHolder(private val binding: RecyclerViewItemMoneyMovingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(moneyMovement: FullMoneyMoving) {
            with(binding) {
                dataTime.text = moneyMovement.timeStamp.parseTimeFromMillis()
                cashAccountName.text = moneyMovement.cashAccountNameValue
                currencyName.text = moneyMovement.currencyNameValue
                categoryName.text = moneyMovement.categoryNameValue
                if (!moneyMovement.description.isNullOrEmpty()) {
                    description.text = moneyMovement.description
                    description.visibility = View.VISIBLE
                }

                if (moneyMovement.isIncome) {
                    amount.text = positive + moneyMovement.amount.toString()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.amount.setTextColor(
                            itemView.resources.getColor(
                                R.color.incomeTextColor,
                                null
                            )
                        )
                    }
                }
                if (!moneyMovement.isIncome) {
                    amount.text = negative + moneyMovement.amount.toString()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.amount.setTextColor(
                            itemView.resources.getColor(
                                R.color.spendingTextColor,
                                null
                            )
                        )
                    }
                }

                moneyMovingItem.setOnClickListener {
                    moneyMovement.id.let { listener.onClick(it) }
                }
            }
        }
    }
}