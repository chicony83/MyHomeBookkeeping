package com.chico.myhomebookkeeping.ui.reports.main

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.chico.myhomebookkeeping.R
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Charts {
    fun showDonutChart(
        context: Context,
        chartView: PieChart,
        items: List<ReportCategoryItem>,
        centerAmountText: String,
        centerLabelText: String
    ) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = context.getString(R.string.chart_label_report)
        val totalAmount = items.sumOf { it.amount }
        for (item in items) {
            pieEntries.add(PieEntry(item.amount.toFloat(), item.displayName))
        }

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 10f
        pieDataSet.colors = items.map { it.color }
        pieDataSet.setValueTextColors(items.map { readableTextColor(it.color) })
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueFormatter = SmallSliceValueFormatter(totalAmount)

        val legend = chartView.legend
        legend.isEnabled = false
        chartView.description.isEnabled = false
        chartView.isDrawHoleEnabled = true
        chartView.holeRadius = 58f
        chartView.transparentCircleRadius = 62f
        chartView.setDrawEntryLabels(false)
        chartView.setUsePercentValues(true)
        chartView.centerText = createCenterText(context, centerAmountText, centerLabelText)
        chartView.setCenterTextSize(22f)

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        chartView.setData(pieData)
        chartView.animateX(1000)
        chartView.invalidate()
    }

    private fun createCenterText(
        context: Context,
        centerAmountText: String,
        centerLabelText: String
    ): SpannableString {
        val centerText = "$centerAmountText\n$centerLabelText"
        val spannable = SpannableString(centerText)
        val labelStart = centerAmountText.length + 1
        spannable.setSpan(
            RelativeSizeSpan(1f),
            0,
            centerAmountText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.52f),
            labelStart,
            centerText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color)),
            0,
            centerAmountText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_second_color)),
            labelStart,
            centerText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private class SmallSliceValueFormatter(
        private val totalAmount: Double
    ) : ValueFormatter() {
        private val amountFormatter = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.getDefault()))
        private val percentFormatter = DecimalFormat("0.#", DecimalFormatSymbols(Locale.getDefault()))

        override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
            if (totalAmount <= 0.0 || value < MIN_VISIBLE_PERCENT) return ""
            val amount = pieEntry?.value ?: 0f
            return "${amountFormatter.format(amount)}\n${percentFormatter.format(value)}%"
        }

        companion object {
            private const val MIN_VISIBLE_PERCENT = 4f
        }
    }

    private fun readableTextColor(backgroundColor: Int): Int {
        val red = Color.red(backgroundColor)
        val green = Color.green(backgroundColor)
        val blue = Color.blue(backgroundColor)
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        return if (luminance > 0.62) Color.rgb(23, 35, 38) else Color.WHITE
    }
}
