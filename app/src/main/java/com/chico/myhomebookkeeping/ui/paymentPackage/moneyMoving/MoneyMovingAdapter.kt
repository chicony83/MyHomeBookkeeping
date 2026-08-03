package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemMoneyMovingDateSeparatorBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemMoneyMovingBinding
import com.chico.myhomebookkeeping.db.full.FullMoneyMoving
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.DayNightMode
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import com.chico.myhomebookkeeping.utils.parseTimeFromMillisShortDate
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MoneyMovingAdapter(
    private val moneyMovementList: List<FullMoneyMoving>,
    private val currencyDisplayMode: String,
    private val showDateSeparators: Boolean,
    private val listener: OnItemViewClickListenerLong
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val adapterItems = createAdapterItems()
    private lateinit var plus: String
    private lateinit var minus: String
    private lateinit var context: Context
    private val uiHelper = UiHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        getStrings()
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DATE_SEPARATOR -> ViewHolderDateSeparator(
                RecyclerViewItemMoneyMovingDateSeparatorBinding.inflate(inflater, parent, false)
            )
            else -> ViewHolderMovingItem(
                RecyclerViewItemMoneyMovingBinding.inflate(inflater, parent, false)
            )
        }
    }

    private fun getStrings() {
        plus = context.getString(R.string.sign_plus)
        minus = context.getString(R.string.sign_minus)
    }

    override fun getItemViewType(position: Int): Int {
        return when (adapterItems[position]) {
            is JournalItem.DateSeparator -> VIEW_TYPE_DATE_SEPARATOR
            is JournalItem.MoneyMovementItem -> VIEW_TYPE_MONEY_MOVEMENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = adapterItems[position]) {
            is JournalItem.DateSeparator -> (holder as ViewHolderDateSeparator).bind(item.timeStamp)
            is JournalItem.MoneyMovementItem -> (holder as ViewHolderMovingItem).bind(item.moneyMovement)
        }
    }

    override fun getItemCount() = adapterItems.size

    private fun createAdapterItems(): List<JournalItem> {
        if (!showDateSeparators) {
            return moneyMovementList.map { JournalItem.MoneyMovementItem(it) }
        }

        val items = mutableListOf<JournalItem>()
        var previousDayKey: String? = null
        moneyMovementList.forEach { moneyMovement ->
            val currentDayKey = moneyMovement.timeStamp.dayKey()
            if (currentDayKey != previousDayKey) {
                items.add(JournalItem.DateSeparator(moneyMovement.timeStamp))
                previousDayKey = currentDayKey
            }
            items.add(JournalItem.MoneyMovementItem(moneyMovement))
        }
        return items
    }

    private fun Long.dayKey(): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = this@dayKey }
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
    }

    private sealed class JournalItem {
        data class DateSeparator(val timeStamp: Long) : JournalItem()
        data class MoneyMovementItem(val moneyMovement: FullMoneyMoving) : JournalItem()
    }

    inner class ViewHolderDateSeparator(
        private val binding: RecyclerViewItemMoneyMovingDateSeparatorBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeStamp: Long) {
            binding.dateSeparatorText.text = dateSeparatorTitle(timeStamp)
        }

        private fun dateSeparatorTitle(timeStamp: Long): String {
            val date = Date(timeStamp)
            val formattedDate = DateFormat
                .getDateInstance(DateFormat.LONG, Locale.getDefault())
                .format(date)
            val relativeTitle = relativeDateTitle(timeStamp)
            return relativeTitle?.let { "$it, $formattedDate" } ?: formattedDate
        }

        private fun relativeDateTitle(timeStamp: Long): String? {
            val today = Calendar.getInstance().dayStart()
            val itemDate = Calendar.getInstance().apply { timeInMillis = timeStamp }
            val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
            val dayBeforeYesterday = (today.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -2)
            }
            return when {
                itemDate.isSameDay(today) -> context.getString(R.string.journal_date_today)
                itemDate.isSameDay(yesterday) -> context.getString(R.string.journal_date_yesterday)
                itemDate.isSameDay(dayBeforeYesterday) ->
                    context.getString(R.string.journal_date_day_before_yesterday)
                else -> null
            }
        }

        private fun Calendar.dayStart(): Calendar {
            return (clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        private fun Calendar.isSameDay(other: Calendar): Boolean {
            return get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
        }
    }

    inner class ViewHolderMovingItem(
        private val binding: RecyclerViewItemMoneyMovingBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val isNightMode: Boolean = DayNightMode.isNightMode

        @SuppressLint("SetTextI18n")
        fun bind(moneyMovement: FullMoneyMoving) {
            with(binding) {
                if (!isNightMode) {
                    item.setBackgroundResource(R.drawable.money_moving_day_item_background)
                }
                if (isNightMode) {
                    cardView.setBackgroundResource(R.drawable.money_moving_night_item_background)
                }
//                if (showDate) {
//                    dateSeparatorText.text =
//                        moneyMovement.timeStamp.parseTimeFromMillisShortDate()
//                    dateSeparatorText.visibility = View.VISIBLE
//                }
//                dateSeparatorText.text = moneyMovement.timeStamp.parseTimeFromMillisShortDate()

                dataTime.text = moneyMovement.timeStamp.parseTimeFromMillisShortDate()
                cashAccountName.text = moneyMovement.cashAccountNameValue
                currencyName.text = currencyTitle(moneyMovement)
                bindCategoryName(moneyMovement)

                if (!moneyMovement.description.isNullOrEmpty()) {
                    description.text = moneyMovement.description
                    uiHelper.showUiElement(description)
//                    val text = moneyMovement.description.toString()
//                    val numOfLines = countLines(text)
//                    if (numOfLines > 2) {
//                        val array = textToArray(text)
//                        val newArray: MutableList<String> = changeArray(array)
//                        description.text = newArray.joinToString()
//                        uiHelper.showUiElement(description)
//                        uiHelper.showUiElement(descriptionOfDescription)
//                    }
                }
                if (moneyMovement.description.isNullOrEmpty()) {
                    description.text = null
                    uiHelper.hideUiElement(description)
                }
                if (moneyMovement.paymentTypeId == PaymentTypeIds.INCOME) {
                    amountEditText.text = plus + moneyMovement.amount.toString()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        with(binding){
                            amountEditText.setTextColor(
                                itemView.resources.getColor(
                                    R.color.incomeTextColor,
                                    null
                                )
                            )
                        }
                    }
                }
                if (moneyMovement.paymentTypeId == PaymentTypeIds.SPENDING) {
                    amountEditText.text = minus + moneyMovement.amount.toString()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.amountEditText.setTextColor(
                            itemView.resources.getColor(
                                R.color.spendingTextColor,
                                null
                            )
                        )
                    }
                }
                if (moneyMovement.paymentTypeId == PaymentTypeIds.TRANSFER) {
                    amountEditText.text = if (moneyMovement.transferDirection == PaymentTypeIds.TRANSFER_DIRECTION_FROM) {
                        minus + moneyMovement.amount.toString()
                    } else {
                        plus + moneyMovement.amount.toString()
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        amountEditText.setTextColor(
                            itemView.resources.getColor(
                                R.color.categoryTextSecondary,
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

        private fun bindCategoryName(moneyMovement: FullMoneyMoving) {
            with(binding) {
                val parentName = moneyMovement.parentCategoryNameValue
                val category = moneyMovement.categoryNameValue
                val singleLineCategory =
                    moneyMovement.categoryDisplayName ?: moneyMovement.paymentTypeName

                categoryName.text = singleLineCategory
                childCategoryName.visibility = View.GONE
                childCategoryName.text = null

                if (parentName.isNullOrBlank() || category.isNullOrBlank()) return

                categoryName.post {
                    if (categoryName.text != singleLineCategory) return@post

                    // Split only when the full category path cannot fit next to the date.
                    val isSingleLineTooLong = categoryName.layout
                        ?.let { it.getEllipsisCount(0) > 0 }
                        ?: false

                    if (isSingleLineTooLong) {
                        categoryName.text = parentName
                        childCategoryName.text = category
                        childCategoryName.visibility = View.VISIBLE
                    }
                }
            }
        }

        private fun currencyTitle(moneyMovement: FullMoneyMoving): String {
            return when (currencyDisplayMode) {
                Constants.JOURNAL_CURRENCY_DISPLAY_SHORT_NAME ->
                    moneyMovement.currencyShortNameValue
                Constants.JOURNAL_CURRENCY_DISPLAY_ISO ->
                    moneyMovement.currencyIsoValue
                else -> moneyMovement.currencyNameValue
            }?.takeIf { it.isNotBlank() } ?: moneyMovement.currencyNameValue
        }

//        private fun changeArray(array: Array<String>): MutableList<String> {
//            val newArray = mutableListOf<String>()
//            for (item in 0..1) {
//                newArray.add(item, array[item])
//            }
//            newArray.add(2, "...")
//            return newArray
//        }
//        private fun textToArray(text: String): Array<String> {
//            return text.split("\n").toTypedArray()
//        }
//        private fun countLines(str: String): Int {
//            val lines: Array<String> = str.split("\n").toTypedArray()
//            return lines.size
//        }
    }

    private companion object {
        const val VIEW_TYPE_DATE_SEPARATOR = 0
        const val VIEW_TYPE_MONEY_MOVEMENT = 1
    }
}
