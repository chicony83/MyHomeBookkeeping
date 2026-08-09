package com.chico.myhomebookkeeping.ui.reports.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chico.myhomebookkeeping.R
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemReportBreakdownBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ReportsBreakdownAdapter(
    private val list: List<ReportCategoryItem>,
    private val currencyShortName: String?
) : RecyclerView.Adapter<ReportsBreakdownAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemReportBreakdownBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemReportBreakdownBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportCategoryItem) {
            with(binding) {
                categoryNameTextView.text = item.displayName
                amountTextView.text = formatAmount(item.amount, currencyShortName)
                percentTextView.text = formatPercent(item.percentage)
                progressBar.progress = item.percentage.toInt().coerceIn(0, 100)
                progressBar.progressTintList = ColorStateList.valueOf(item.color)
                categoryIconImageView.setImageResource(item.iconRes ?: R.drawable.no_image)
                categoryIconImageView.imageTintList = ColorStateList.valueOf(Color.WHITE)
                categoryIconImageView.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(item.color)
                }
            }
        }
    }

    private fun formatAmount(amount: Double, currencyShortName: String?): String {
        val formatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.getDefault()))
        val formattedAmount = formatter.format(amount)
        return if (currencyShortName.isNullOrBlank()) {
            formattedAmount
        } else {
            "$formattedAmount $currencyShortName"
        }
    }

    private fun formatPercent(percentage: Double): String {
        val formatter = DecimalFormat("0.0", DecimalFormatSymbols(Locale.getDefault()))
        return "${formatter.format(percentage)} %"
    }
}
