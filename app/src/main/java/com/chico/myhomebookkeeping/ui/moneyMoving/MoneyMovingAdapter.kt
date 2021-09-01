package com.chico.myhomebookkeeping.ui.moneyMoving

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemMoneyMovingBinding
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate
import java.util.*

class MoneyMovingAdapter(
    private val moneyMovementList: List<FullMoneyMoving>,
    val listener: OnItemViewClickListenerLong
) : RecyclerView.Adapter<MoneyMovingAdapter.ViewHolderMovingItem>() {
    private lateinit var positive: String
    private lateinit var negative: String
    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    private var dayToday: Long = 0
    private var dayYesterday: Long = 0
    private lateinit var context: Context
    private val uiHelper = UiHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMovingItem {
        val binding = RecyclerViewItemMoneyMovingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        getStrings()
        val isNightMode = checkIsNightUiMode()
        return ViewHolderMovingItem(binding,isNightMode)
    }

    private fun checkIsNightUiMode(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                false
            }
            else -> {
                true
            }
        }
    }

    private fun getStrings() {
        positive = context.getString(R.string.positive_value)
        negative = context.getString(R.string.negative_value)
    }

    override fun onBindViewHolder(holder: ViewHolderMovingItem, position: Int) {

//        var showDate = checkTodayAndYesterdayIsOneDate(position)
        holder.bind(moneyMovementList[position])
//        holder.bind(moneyMovementList[position], showDate)
    }

    private fun checkTodayAndYesterdayIsOneDate(position: Int): Boolean {
        dayToday = moneyMovementList[position].timeStamp
        calendar.timeInMillis = dayToday
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = dayYesterday
        val yesterday = calendar.get(Calendar.DAY_OF_YEAR)
        var showDate = false
        if (today == yesterday) {
            showDate = false
        }
        if (today != yesterday) {
            showDate = true
            dayYesterday = moneyMovementList[position].timeStamp
        }
        return showDate
    }

    override fun getItemCount() = moneyMovementList.size

    inner class ViewHolderMovingItem(
        private val binding: RecyclerViewItemMoneyMovingBinding,
        private val isNightMode: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(moneyMovement: FullMoneyMoving) {

            with(binding) {
                if (!isNightMode){
                    item.setBackgroundResource(R.drawable.money_moving_day_item_background)
                }
//                if (showDate) {
//                    dateSeparatorText.text =
//                        moneyMovement.timeStamp.parseTimeFromMillisShortDate()
//                    dateSeparatorText.visibility = View.VISIBLE
//                }
//                dateSeparatorText.text = moneyMovement.timeStamp.parseTimeFromMillisShortDate()
                dataTime.text = moneyMovement.timeStamp.parseTimeFromMillisShortDate()
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

    private fun messageLog(text: String) {
        Log.i("TAG", "$text")
    }
}