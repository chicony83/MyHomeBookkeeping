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
import java.util.*

class MoneyMovingAdapter(
    private val moneyMovementList: List<FullMoneyMoving>,
    val listener: OnItemViewClickListenerLong
) : RecyclerView.Adapter<MoneyMovingAdapter.ViewHolderMovingItem>() {
    private lateinit var positive: String
    private lateinit var negative: String
    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMovingItem {
        val binding = RecyclerViewItemMoneyMovingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val context = parent.context
        positive = context.getString(R.string.positive_value)
        negative = context.getString(R.string.negative_value)
        return ViewHolderMovingItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderMovingItem, position: Int) {
        holder.bind(moneyMovementList[position])
    }

    override fun getItemCount() = moneyMovementList.size

//    inner class ViewHolderDateItem():RecyclerView.ViewHolder(binding.root)
    inner class ViewHolderMovingItem(
        private val binding: RecyclerViewItemMoneyMovingBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(moneyMovement: FullMoneyMoving) {
            with(binding) {
                calendar.timeInMillis = moneyMovement.timeStamp
                val date: Int = calendar.get(Calendar.DAY_OF_YEAR)
                dateSeparatorText.text = date.toString()

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